package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.*;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.*;
import com.recommendationSys.Sistema_Recomendador_Finales.services.ExcelProcessingUtils;
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

    @Override
    public HistoriaAcademica procesarArchivoExcel(MultipartFile file, Long estudianteId) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        PlanDeEstudio plan = obtenerPlanDeEstudio(sheet);
        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        HistoriaAcademica historia = obtenerOCrearHistoria(estudiante, plan);

        procesarFilasExcel(sheet, historia, plan);

        return historia;
    }

    private PlanDeEstudio obtenerPlanDeEstudio(Sheet sheet) {
        String nombrePlan = sheet.getRow(3).getCell(0).getStringCellValue().trim();
        return (PlanDeEstudio) planRepo.findByPropuesta(nombrePlan)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de estudio no encontrado: " + nombrePlan));
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

    private void procesarFilasExcel(Sheet sheet, HistoriaAcademica historia, PlanDeEstudio plan) {
        int lastRow = ExcelProcessingUtils.obtenerUltimaFilaConDatos(sheet);

        for (int i = 6; i <= lastRow; i++) {
            procesarFila(sheet.getRow(i), historia, plan);
        }
        // Borro las promociones
        renglonRepo.deleteByTipoAndResultado("Promocion","Promocionado");
    }

    private void procesarFila(Row row, HistoriaAcademica historia, PlanDeEstudio plan) {
        if (row == null || ExcelProcessingUtils.isEmptyRow(row)) return;

        DatosFilaExcel datos = extraerDatosFila(row);
        if (debeOmitirFila(datos)) return;

        try {
            Materia materia = obtenerMateria(datos.nombreMateria(), plan);
            procesarRenglonSegunTipo(datos, historia, materia);
        } catch (ResourceNotFoundException e) {
            log.warn("Fila salteada - Materia no encontrada: '{}'. Detalle: {}", datos.nombreMateria(), e.getMessage());
        }
    }

    private DatosFilaExcel extraerDatosFila(Row row) {
        String nombreMateria = row.getCell(0).getStringCellValue().trim();
        nombreMateria = nombreMateria.substring(0, nombreMateria.indexOf("(")).trim();
        LocalDate fecha = LocalDate.parse(
                row.getCell(1).getStringCellValue().trim(),
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
        String tipo = row.getCell(2).getStringCellValue().trim();
        Double nota = ExcelProcessingUtils.extraerNota(row.getCell(3));
        String resultado = row.getCell(4).getStringCellValue().trim();

        return new DatosFilaExcel(nombreMateria, fecha, tipo, nota, resultado);
    }

    private boolean debeOmitirFila(DatosFilaExcel datos) {
        return "En curso".equalsIgnoreCase(datos.tipo()) ||
                ("Regularidad".equalsIgnoreCase(datos.tipo()) &&
                        ("Reprobado".equalsIgnoreCase(datos.resultado()) ||
                                "Ausente".equalsIgnoreCase(datos.resultado()))) || ("Examen".equalsIgnoreCase(datos.tipo()) && ("Ausente".equalsIgnoreCase(datos.resultado())))
                ;
    }

    private Materia obtenerMateria(String nombreMateria, PlanDeEstudio plan) {
        return materiaRepo.findByNombreAndPlanDeEstudio(nombreMateria, plan)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada: " + nombreMateria));
    }

    private void procesarRenglonSegunTipo(DatosFilaExcel datos, HistoriaAcademica historia, Materia materia) {
        switch (datos.tipo().toLowerCase()) {
            case "regularidad":
                procesarRegularidad(datos, historia, materia);
                break;
            case "examen":
                procesarExamen(datos, historia, materia);
                break;
            case "promocion":
                procesarPromocion(datos, historia, materia);
                break;
        }
    }

    private void procesarRegularidad(DatosFilaExcel datos, HistoriaAcademica historia, Materia materia) {
        if (tieneExamenAprobadoOMateriaPromocionada(materia, historia)) return;

        Renglon renglon = renglonFactory.crearRenglon(
                datos.fecha(), datos.tipo(), datos.nota(), datos.resultado(), historia, materia
        );
        renglonRepo.save(renglon);
    }

    private boolean tieneExamenAprobadoOMateriaPromocionada(Materia materia, HistoriaAcademica historia) {
        return renglonRepo.existsByMateriaAndHistoriaAcademicaAndTipoAndNotaGreaterThanEqual(
                materia, historia, "Examen", 4.0) ||
                renglonRepo.existsByMateriaAndHistoriaAcademicaAndTipoAndResultado(
                        materia, historia, "Promocion", "Promocionado");
    }

    private void procesarExamen(DatosFilaExcel datos, HistoriaAcademica historia, Materia materia) {
        Renglon renglon = renglonFactory.crearRenglon(
                datos.fecha(), datos.tipo(), datos.nota(), datos.resultado(), historia, materia
        );
        renglonRepo.save(renglon);

        if (datos.nota() != null) {
            examenRepo.save(new Examen(datos.fecha(), datos.nota(), renglon));
            if(datos.nota() >= 4.0) {
                eliminarRegularidadSiExiste(materia, historia);
            }
        }
    }

    private void procesarPromocion(DatosFilaExcel datos, HistoriaAcademica historia, Materia materia) {
        if ("Promocionado".equalsIgnoreCase(datos.resultado())) {
            Renglon renglon = renglonFactory.crearRenglon(
                    datos.fecha(), datos.tipo(), datos.nota(), datos.resultado(), historia, materia
            );
            renglonRepo.save(renglon);
            eliminarRegularidadSiExiste(materia, historia);
        }
    }

    private void eliminarRegularidadSiExiste(Materia materia, HistoriaAcademica historia) {
        renglonRepo.findByMateriaAndHistoriaAcademicaAndTipoAndResultado(
                materia, historia, "Regularidad", "Aprobado"
        ).ifPresent(renglonRepo::delete);
    }
    @Override
    public HistoriaAcademica procesarArchivoExcelActualizacion(MultipartFile file, Long estudianteId) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        PlanDeEstudio plan = obtenerPlanDeEstudio(sheet);
        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        HistoriaAcademica historia = historiaRepo.findByEstudiante(estudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada para actualización"));

        procesarFilasExcelConChequeo(sheet, historia, plan);
        renglonRepo.deleteByTipoAndResultado("Promocion","Promocionado");

        return historia;
    }

    private void procesarFilasExcelConChequeo(Sheet sheet, HistoriaAcademica historia, PlanDeEstudio plan) {
        int lastRow = ExcelProcessingUtils.obtenerUltimaFilaConDatos(sheet);

        for (int i = 6; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null || ExcelProcessingUtils.isEmptyRow(row)) continue;

            DatosFilaExcel datos = extraerDatosFila(row);
            if (debeOmitirFila(datos)) continue;

            Materia materia;
            try {
                materia = obtenerMateria(datos.nombreMateria(), plan);
            } catch (ResourceNotFoundException e) {
                log.warn("Materia no encontrada en actualización: {}", datos.nombreMateria());
                continue;
            }

            boolean yaExiste = renglonRepo.existsByMateriaAndHistoriaAcademicaAndTipoAndFechaAndResultado(
                    materia, historia, datos.tipo(), datos.fecha(), datos.resultado());

            if (yaExiste) continue;

            procesarRenglonSegunTipo(datos, historia, materia);
        }
    }

}
