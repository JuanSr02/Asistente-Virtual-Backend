package com.asistenteVirtual.services.historiaAcademica;

import com.asistenteVirtual.exceptions.PlanIncompatibleException;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.*;
import com.asistenteVirtual.repository.*;
import com.asistenteVirtual.services.ExcelProcessingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelProcessingServiceImpl implements ExcelProcessingService {

    private final HistoriaAcademicaRepository historiaRepo;
    private final EstudianteRepository estudianteRepo;
    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final RenglonRepository renglonRepo;
    private final ExamenRepository examenRepo;
    private final RenglonFactory renglonFactory;

    private static final double UMBRAL_COINCIDENCIA = 70.0;

    @Override
    public HistoriaAcademica procesarArchivoExcel(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        PlanDeEstudio plan = obtenerPlanDeEstudio(codigoPlan);
        Map<String, Materia> materiasDelPlanMap = materiaRepo.findByPlanDeEstudio_Codigo(plan.getCodigo()).stream()
                .collect(Collectors.toMap(Materia::getNombre, Function.identity()));

        // --- PASO CLAVE: VALIDACIÓN HEURÍSTICA ---
        validarCoincidenciaDelPlan(sheet, materiasDelPlanMap);

        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        HistoriaAcademica historia = obtenerOCrearHistoria(estudiante, plan);

        procesarFilasExcel(sheet, historia, materiasDelPlanMap); // Pasamos el mapa que ya tenemos

        return historia;
    }

    private void validarCoincidenciaDelPlan(Sheet sheet, Map<String, Materia> materiasDelPlanMap) {
        int lastRow = ExcelProcessingUtils.obtenerUltimaFilaConDatos(sheet);
        int materiasEncontradas = 0;
        int materiasNoEncontradas = 0;

        for (int i = 6; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null || ExcelProcessingUtils.isEmptyRow(row)) continue;

            // Solo necesitamos el nombre de la materia para la validación
            String nombreMateriaExcel = row.getCell(0).getStringCellValue().trim();
            nombreMateriaExcel = nombreMateriaExcel.substring(0, nombreMateriaExcel.indexOf("(")).trim();

            if (materiasDelPlanMap.containsKey(nombreMateriaExcel)) {
                materiasEncontradas++;
            } else {
                // No contamos regularidades reprobadas o ausentes como "no encontradas"
                // porque podrían ser de materias que sí existen pero no nos interesan.
                // Es mejor contar todas las filas válidas.
                materiasNoEncontradas++;
            }
        }

        if (materiasEncontradas + materiasNoEncontradas == 0) {
            throw new PlanIncompatibleException("El archivo parece estar vacío o en un formato incorrecto.");
        }

        double porcentajeCoincidencia = ((double) materiasEncontradas / (materiasEncontradas + materiasNoEncontradas)) * 100.0;
        log.info("Análisis de coincidencia: {}% de materias encontradas para el plan seleccionado.", String.format("%.2f", porcentajeCoincidencia));

        if (porcentajeCoincidencia < UMBRAL_COINCIDENCIA) {
            throw new PlanIncompatibleException(
                    String.format(
                            "El archivo no parece corresponder al plan seleccionado. Solo el %.2f%% de las materias coincidieron (se requiere al menos %.0f%%).",
                            porcentajeCoincidencia, UMBRAL_COINCIDENCIA
                    )
            );
        }
    }


    private PlanDeEstudio obtenerPlanDeEstudio(String codigoPlan) {
        return planRepo.findById(codigoPlan)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de estudio con codigo: " + codigoPlan + " no encontrado"));
    }

    private HistoriaAcademica obtenerOCrearHistoria(Estudiante estudiante, PlanDeEstudio plan) {
        return historiaRepo.findByEstudiante(estudiante)
                .orElseGet(() -> {
                    HistoriaAcademica h = new HistoriaAcademica();
                    h.setEstudiante(estudiante);
                    h.setPlanDeEstudio(plan);
                    return historiaRepo.save(h);
                });
    }

    private void procesarFilasExcel(Sheet sheet, HistoriaAcademica historia, Map<String, Materia> materiasMap) {
        int lastRow = ExcelProcessingUtils.obtenerUltimaFilaConDatos(sheet);
        List<Renglon> renglonList = new ArrayList<>();
        List<Examen> examenList = new ArrayList<>();
        for (int i = 6; i <= lastRow; i++) {
            procesarFila(sheet.getRow(i), historia, materiasMap, renglonList, examenList);
        }
        // Borro las promociones
        List<Renglon> aEliminar = renglonList.stream()
                .filter(r -> "Promocion".equalsIgnoreCase(r.getTipo()) &&
                        "Promocionado".equalsIgnoreCase(r.getResultado()))
                .collect(Collectors.toList());

        renglonList.removeAll(aEliminar); // mantiene la lista en memoria actualizada
        renglonRepo.saveAll(renglonList);
        examenRepo.saveAll(examenList);
    }

    private void procesarFila(Row row, HistoriaAcademica historia, Map<String, Materia> materiasMap, List<Renglon> renglonList, List<Examen> examenList) {
        if (row == null || ExcelProcessingUtils.isEmptyRow(row)) return;

        DatosFilaExcel datos = extraerDatosFila(row);
        if (debeOmitirFila(datos)) return;

        Materia materia = materiasMap.get(datos.nombreMateria());
        if (materia == null) {
            log.warn("Materia no encontrada: {}", datos.nombreMateria());
            return;
        }
        procesarRenglonSegunTipo(datos, historia, materia, renglonList, examenList);

    }

    private DatosFilaExcel extraerDatosFila(Row row) {
        String nombreMateria = row.getCell(0).getStringCellValue().trim();
        String codigo = nombreMateria.substring(nombreMateria.indexOf("(") + 1, nombreMateria.indexOf(")")).trim();
        nombreMateria = nombreMateria.substring(0, nombreMateria.indexOf("(")).trim();
        LocalDate fecha = LocalDate.parse(
                row.getCell(1).getStringCellValue().trim(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        String tipo = row.getCell(2).getStringCellValue().trim();
        Double nota = ExcelProcessingUtils.extraerNota(row.getCell(3));
        String resultado = row.getCell(4).getStringCellValue().trim();

        return new DatosFilaExcel(nombreMateria, codigo, fecha, tipo, nota, resultado);
    }

    private boolean debeOmitirFila(DatosFilaExcel datos) {
        return "En curso".equalsIgnoreCase(datos.tipo()) ||
                ("Regularidad".equalsIgnoreCase(datos.tipo()) &&
                        ("Reprobado".equalsIgnoreCase(datos.resultado()) ||
                                "Ausente".equalsIgnoreCase(datos.resultado()))) || ("Examen".equalsIgnoreCase(datos.tipo()) && ("Ausente".equalsIgnoreCase(datos.resultado())))
                ;
    }


    private void procesarRenglonSegunTipo(DatosFilaExcel datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList, List<Examen> examenList) {
        switch (datos.tipo().toLowerCase()) {
            case "regularidad":
                procesarRegularidad(datos, historia, materia, renglonList);
                break;
            case "examen":
                procesarExamen(datos, historia, materia, renglonList, examenList);
                break;
            case "promocion":
                procesarPromocion(datos, historia, materia, renglonList);
                break;
        }
    }

    private void procesarRegularidad(DatosFilaExcel datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList) {
        if (tieneExamenAprobadoOMateriaPromocionada(materia, historia, renglonList)) return;

        Renglon renglon = renglonFactory.crearRenglon(
                datos.fecha(), datos.tipo(), datos.nota(), datos.resultado(), historia, materia
        );
        renglonList.add(renglon);
    }

    private boolean tieneExamenAprobadoOMateriaPromocionada(Materia materia, HistoriaAcademica historia, List<Renglon> renglonList) {
        return renglonList.stream().anyMatch(r ->
                r.getMateria().equals(materia)
                        && r.getHistoriaAcademica().equals(historia)
                        && (
                        ("Examen".equalsIgnoreCase(r.getTipo()) && r.getNota() != null && r.getNota() >= 4.0) ||
                                ("Promocion".equalsIgnoreCase(r.getTipo()) && "Promocionado".equalsIgnoreCase(r.getResultado()))
                )
        );
    }


    private void procesarExamen(DatosFilaExcel datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList, List<Examen> examenList) {
        Renglon renglon = renglonFactory.crearRenglon(
                datos.fecha(), datos.tipo(), datos.nota(), datos.resultado(), historia, materia
        );
        renglonList.add(renglon);

        if (datos.nota() != null) {
            examenList.add(new Examen(datos.fecha(), datos.nota(), renglon));
            if (datos.nota() >= 4.0) {
                eliminarRegularidadSiExiste(materia, historia, renglonList);
            }
        }
    }

    private void procesarPromocion(DatosFilaExcel datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList) {
        if ("Promocionado".equalsIgnoreCase(datos.resultado())) {
            Renglon renglon = renglonFactory.crearRenglon(
                    datos.fecha(), datos.tipo(), datos.nota(), datos.resultado(), historia, materia
            );
            renglonList.add(renglon);
            eliminarRegularidadSiExiste(materia, historia, renglonList);
        }
    }

    private void eliminarRegularidadSiExiste(Materia materia, HistoriaAcademica historia, List<Renglon> renglonList) {
        renglonList.stream()
                .filter(r ->
                        r.getMateria().equals(materia) &&
                                r.getHistoriaAcademica().equals(historia) &&
                                "Regularidad".equalsIgnoreCase(r.getTipo()) &&
                                "Aprobado".equalsIgnoreCase(r.getResultado())
                )
                .findFirst()
                .ifPresent(renglon -> {
                    renglonList.remove(renglon); // importante para mantener la lista sincronizada
                });
    }


    @Override
    public HistoriaAcademica procesarArchivoExcelActualizacion(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        PlanDeEstudio plan = obtenerPlanDeEstudio(codigoPlan);
        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        HistoriaAcademica historia = historiaRepo.findByEstudiante(estudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada para actualización"));

        procesarFilasExcelConChequeo(sheet, historia, plan);

        return historia;
    }

    private void procesarFilasExcelConChequeo(Sheet sheet, HistoriaAcademica historia, PlanDeEstudio plan) {
        int lastRow = ExcelProcessingUtils.obtenerUltimaFilaConDatos(sheet);
        List<Renglon> renglonList = renglonRepo.findByHistoriaAcademica(historia);
        List<Examen> examenList = examenRepo.findAllByHistoriaAcademica(historia);
        List<Renglon> renglonesOriginales = renglonList;
        List<Examen> examenesOriginales = examenList;
        Map<String, Materia> materiasMap = materiaRepo.findByPlanDeEstudio_Codigo(plan.getCodigo()).stream()
                .collect(Collectors.toMap(Materia::getNombre, Function.identity()));

        for (int i = 6; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null || ExcelProcessingUtils.isEmptyRow(row)) continue;

            DatosFilaExcel datos = extraerDatosFila(row);
            if (debeOmitirFila(datos)) continue;

            Materia materia = materiasMap.get(datos.nombreMateria());
            if (materia == null) {
                log.warn("Materia no encontrada: {}", datos.nombreMateria());
                continue;
            }

            Renglon re = Renglon.builder().materia(materia).historiaAcademica(historia).tipo(datos.tipo()).fecha(datos.fecha()).resultado(datos.resultado()).build();
            boolean yaExiste = renglonList.contains(re);

            if (yaExiste) continue;

            procesarRenglonSegunTipo(datos, historia, materia, renglonList, examenList);
            // Borro las promociones
            List<Renglon> aEliminar = renglonList.stream()
                    .filter(r -> "Promocion".equalsIgnoreCase(r.getTipo()) &&
                            "Promocionado".equalsIgnoreCase(r.getResultado()))
                    .collect(Collectors.toList());

            renglonList.removeAll(aEliminar); // mantiene la lista en memoria actualizada
            renglonList.removeAll(renglonesOriginales);
            examenList.removeAll(examenesOriginales);
            if (!renglonList.isEmpty()) {
                renglonRepo.saveAll(renglonList);
            }
            if (!examenList.isEmpty()) {
                examenRepo.saveAll(examenList);
            }
        }
    }

}
