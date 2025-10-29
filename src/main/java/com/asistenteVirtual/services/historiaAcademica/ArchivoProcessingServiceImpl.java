package com.asistenteVirtual.services.historiaAcademica;

import com.asistenteVirtual.exceptions.PlanIncompatibleException;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.*;
import com.asistenteVirtual.repository.*;
import com.asistenteVirtual.services.ExcelProcessingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ArchivoProcessingServiceImpl implements ArchivoProcessingService {

    private final HistoriaAcademicaRepository historiaRepo;
    private final EstudianteRepository estudianteRepo;
    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final RenglonRepository renglonRepo;
    private final ExamenRepository examenRepo;
    private final RenglonFactory renglonFactory;

    private static final double UMBRAL_COINCIDENCIA = 80.0;

    @Override
    public HistoriaAcademica procesarArchivoExcel(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        PlanDeEstudio plan = obtenerPlanDeEstudio(codigoPlan);
        Map<String, Materia> materiasDelPlanMap = obtenerMaterialesParaValidacion(codigoPlan);

        List<DatosFila> datosExtraidos = extraerDatosDeExcel(sheet);

        // --- PASO CLAVE: VALIDACIÓN HEURÍSTICA PARA CHEQUEAR QUE EL PLAN ELEGIDO CONCUERDA CON LA HISTORIA ---
        validarCoincidenciaDelPlan(datosExtraidos, materiasDelPlanMap, codigoPlan);

        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        HistoriaAcademica historia = obtenerOCrearHistoria(estudiante, plan);

        procesarDatos(datosExtraidos, historia, materiasDelPlanMap);
        return historia;
    }


    private Map<String, Materia> obtenerMaterialesParaValidacion(String codigoPlan) {
        Map<String, Materia> materiasMap = new HashMap<>();

        // Agregar materias del plan principal
        List<Materia> materiasDelPlan = materiaRepo.findByPlanDeEstudio_Codigo(codigoPlan);
        materiasDelPlan.forEach(materia -> materiasMap.put(materia.getNombre(), materia));

        return materiasMap;
    }

    private void validarCoincidenciaDelPlan(List<DatosFila> datosExtraidos, Map<String, Materia> materiasDelPlanMap, String codigoPlan) {
        int materiasEncontradas = 0;
        int materiasNoEncontradas = 0;

        for (DatosFila datos : datosExtraidos) {
            if (materiasDelPlanMap.containsKey(datos.nombreMateria())) {
                materiasEncontradas++;
            } else {
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

    private void procesarDatos(List<DatosFila> datosExtraidos, HistoriaAcademica historia, Map<String, Materia> materiasMap) {
        List<Renglon> renglonList = new ArrayList<>();
        List<Examen> examenList = new ArrayList<>();

        for (DatosFila datos : datosExtraidos) {
            Materia materia = materiasMap.get(datos.nombreMateria());
            if (materia == null) {
                log.warn("Materia no encontrada en el plan: {}", datos.nombreMateria());
                continue;
            }
            procesarFila(datos, historia, materiasMap.get(datos.nombreMateria()), renglonList, examenList);
        }

        // Eliminar duplicados antes de guardar
        eliminarDuplicados(renglonList, examenList);

        // Borro las promociones
        List<Renglon> aEliminar = renglonList.stream()
                .filter(r -> "Promocion".equalsIgnoreCase(r.getTipo()) &&
                        "Promocionado".equalsIgnoreCase(r.getResultado()))
                .collect(Collectors.toList());

        renglonList.removeAll(aEliminar);
        renglonRepo.saveAll(renglonList);
        examenRepo.saveAll(examenList);
    }

    private void eliminarDuplicados(List<Renglon> renglonList, List<Examen> examenList) {
        // Eliminar regularidades duplicadas (misma materia y tipo "Regularidad" con resultado "Aprobado")
        Set<Materia> materiasConRegularidad = new HashSet<>();
        List<Renglon> regularidadesDuplicadas = new ArrayList<>();

        for (Renglon renglon : renglonList) {
            if ("Regularidad".equalsIgnoreCase(renglon.getTipo()) &&
                    "Aprobado".equalsIgnoreCase(renglon.getResultado())) {
                if (materiasConRegularidad.contains(renglon.getMateria())) {
                    regularidadesDuplicadas.add(renglon);
                } else {
                    materiasConRegularidad.add(renglon.getMateria());
                }
            }
        }
        renglonList.removeAll(regularidadesDuplicadas);

        // Eliminar exámenes duplicados (misma materia y nota aprobada)
        Map<Materia, Set<Double>> materiasYNotas = new HashMap<>();
        List<Examen> examenesDuplicados = new ArrayList<>();

        for (Examen examen : examenList) {
            if (examen.getNota() >= 4.0) {
                Materia materia = examen.getRenglon().getMateria();
                Double nota = examen.getNota();

                if (!materiasYNotas.containsKey(materia)) {
                    materiasYNotas.put(materia, new HashSet<>());
                }

                if (materiasYNotas.get(materia).contains(nota)) {
                    examenesDuplicados.add(examen);
                    // También eliminar el renglón asociado
                    renglonList.removeIf(r -> r.equals(examen.getRenglon()));
                } else {
                    materiasYNotas.get(materia).add(nota);
                }
            }
        }
        examenList.removeAll(examenesDuplicados);
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
                    h.setEstado("ACTIVA");
                    return historiaRepo.save(h);
                });
    }

    private void procesarFila(DatosFila datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList, List<Examen> examenList) {
        if (debeOmitirFila(datos)) return;
        procesarRenglonSegunTipo(datos, historia, materia, renglonList, examenList);
    }

    private boolean debeOmitirFila(DatosFila datos) {
        return "En curso".equalsIgnoreCase(datos.tipo()) ||
                ("Regularidad".equalsIgnoreCase(datos.tipo()) &&
                        ("Reprobado".equalsIgnoreCase(datos.resultado()) ||
                                "Ausente".equalsIgnoreCase(datos.resultado()))) ||
                ("Examen".equalsIgnoreCase(datos.tipo()) && ("Ausente".equalsIgnoreCase(datos.resultado())));
    }

    private void procesarRenglonSegunTipo(DatosFila datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList, List<Examen> examenList) {
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
            case "equivalencia":
                procesarEquivalencia(datos, historia, materia, renglonList, examenList);
                break;
        }
    }

    private void procesarEquivalencia(DatosFila datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList, List<Examen> examenList) {
        if (datos.nota() != null) {
            // Tiene nota: actúa como examen
            DatosFila datosNew = new DatosFila(datos.nombreMateria(), datos.codigo(), datos.fecha(), "Examen", datos.nota(), datos.resultado());
            procesarExamen(datosNew, historia, materia, renglonList, examenList);
        } else {
            // No tiene nota: actúa como regularidad
            DatosFila datosNew = new DatosFila(datos.nombreMateria(), datos.codigo(), datos.fecha(), "Regularidad", null, datos.resultado());
            procesarRegularidad(datosNew, historia, materia, renglonList);
        }
    }

    private void procesarRegularidad(DatosFila datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList) {
        if (tieneExamenAprobadoOMateriaPromocionada(materia, historia, renglonList)) return;

        Renglon renglon = renglonFactory.crearRenglon(
                datos.fecha(), datos.tipo(), datos.nota(), datos.resultado(), historia, materia
        );
        renglonList.add(renglon);
    }

    private void procesarExamen(DatosFila datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList, List<Examen> examenList) {
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

    private void procesarPromocion(DatosFila datos, HistoriaAcademica historia, Materia materia, List<Renglon> renglonList) {
        if ("Promocionado".equalsIgnoreCase(datos.resultado())) {
            Renglon renglon = renglonFactory.crearRenglon(
                    datos.fecha(), datos.tipo(), datos.nota(), datos.resultado(), historia, materia
            );
            renglonList.add(renglon);
            eliminarRegularidadSiExiste(materia, historia, renglonList);
        }
    }

    private boolean tieneExamenAprobadoOMateriaPromocionada(Materia materia, HistoriaAcademica historia, List<Renglon> renglonList) {
        return renglonList.stream().anyMatch(r ->
                r.getMateria().equals(materia)
                        && r.getHistoriaAcademica().equals(historia)
                        && (
                        ("Examen".equalsIgnoreCase(r.getTipo()) && r.getNota() != null && r.getNota() >= 4.0) ||
                                ("Promocion".equalsIgnoreCase(r.getTipo()) && "Promocionado".equalsIgnoreCase(r.getResultado())) ||
                                ("Equivalencia".equalsIgnoreCase(r.getTipo()) && r.getNota() != null && r.getNota() >= 4.0)
                )
        );
    }

    private void eliminarRegularidadSiExiste(Materia materia, HistoriaAcademica historia, List<Renglon> renglonList) {
        renglonList.stream()
                .filter(r ->
                        r.getMateria().equals(materia) &&
                                r.getHistoriaAcademica().equals(historia) &&
                                ("Regularidad".equalsIgnoreCase(r.getTipo()) ||
                                        ("Equivalencia".equalsIgnoreCase(r.getTipo()) && r.getNota() == null)) &&
                                "Aprobado".equalsIgnoreCase(r.getResultado())
                )
                .findFirst()
                .ifPresent(renglon -> {
                    renglonList.remove(renglon);
                });
    }

    @Override
    public HistoriaAcademica procesarArchivoExcelActualizacion(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado para actualización de historia"));
        HistoriaAcademica historia = historiaRepo.findByEstudiante(estudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada para actualización"));

        Map<String, Materia> materiasMap = obtenerMaterialesParaValidacion(codigoPlan);

        List<DatosFila> datosExtraidos = extraerDatosDeExcel(sheet);
        validarCoincidenciaDelPlan(datosExtraidos, materiasMap, codigoPlan);
        procesarDatosConChequeo(datosExtraidos, historia, materiasMap);
        historia.setEstado("ACTIVA");
        historia = historiaRepo.save(historia);
        return historia;
    }

    private void procesarDatosConChequeo(List<DatosFila> datosExtraidos, HistoriaAcademica historia, Map<String, Materia> materiasMap) {
        List<Renglon> renglonList = renglonRepo.findByHistoriaAcademica(historia);
        List<Examen> examenList = examenRepo.findAllByHistoriaAcademica(historia);
        List<Renglon> renglonesOriginales = new ArrayList<>(renglonList);
        List<Examen> examenesOriginales = new ArrayList<>(examenList);

        for (DatosFila datos : datosExtraidos) {
            if (debeOmitirFila(datos)) continue;

            Materia materia = materiasMap.get(datos.nombreMateria());
            if (materia == null) {
                log.warn("Materia no encontrada en el plan: {}", datos.nombreMateria());
                continue;
            }

            Renglon tempRenglon = Renglon.builder()
                    .materia(materia)
                    .historiaAcademica(historia)
                    .tipo(datos.tipo())
                    .fecha(datos.fecha())
                    .resultado(datos.resultado())
                    .build();

            boolean yaExiste = renglonesOriginales.stream().anyMatch(r ->
                    r.getMateria().equals(tempRenglon.getMateria()) &&
                            r.getTipo().equalsIgnoreCase(tempRenglon.getTipo()) &&
                            r.getFecha().equals(tempRenglon.getFecha()) &&
                            r.getResultado().equalsIgnoreCase(tempRenglon.getResultado())
            );

            if (yaExiste) {
                continue;
            }

            procesarFila(datos, historia, materia, renglonList, examenList);
        }

        // Eliminar duplicados antes de guardar
        eliminarDuplicados(renglonList, examenList);

        List<Renglon> aEliminar = renglonList.stream()
                .filter(r -> "Promocion".equalsIgnoreCase(r.getTipo()) &&
                        "Promocionado".equalsIgnoreCase(r.getResultado()))
                .collect(Collectors.toList());
        renglonList.removeAll(aEliminar);

        List<Renglon> nuevosRenglones = renglonList.stream()
                .filter(r -> !renglonesOriginales.contains(r))
                .collect(Collectors.toList());

        List<Examen> nuevosExamenes = examenList.stream()
                .filter(e -> !examenesOriginales.contains(e))
                .collect(Collectors.toList());

        if (!nuevosRenglones.isEmpty()) {
            renglonRepo.saveAll(nuevosRenglones);
        }
        if (!nuevosExamenes.isEmpty()) {
            examenRepo.saveAll(nuevosExamenes);
        }
    }

    private List<DatosFila> extraerDatosDeExcel(Sheet sheet) {
        List<DatosFila> datos = new ArrayList<>();
        int lastRow = ExcelProcessingUtils.obtenerUltimaFilaConDatos(sheet);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (int i = 6; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (row == null || ExcelProcessingUtils.isEmptyRow(row)) continue;

            try {
                DatosFila datosFila = determinarFormatoYExtraer(row, dateFormatter);
                if (datosFila != null) {
                    datos.add(datosFila);
                }
            } catch (Exception e) {
                log.warn("Error al parsear fila de Excel (fila {}): {}. Se omite la fila.", i, e.getMessage());
            }
        }
        return datos;
    }

    private DatosFila determinarFormatoYExtraer(Row row, DateTimeFormatter dateFormatter) {
        // Verificar si la primera celda es una fecha
        Cell primeraCell = row.getCell(0);
        if (primeraCell == null) return null;

        String primerValor = primeraCell.getStringCellValue().trim();

        // Verificar si es fecha (formato dd/MM/yyyy)
        if (primerValor.matches("\\d{2}/\\d{2}/\\d{4}")) {
            // Formato: fecha, materia, tipo, nota, resultado
            return extraerFormatoFechaMateria(row, dateFormatter);
        } else {
            // Formato: materia, fecha, tipo, nota, resultado
            return extraerFormatoMateriaFecha(row, dateFormatter);
        }
    }

    private DatosFila extraerFormatoFechaMateria(Row row, DateTimeFormatter dateFormatter) {
        LocalDate fecha = LocalDate.parse(row.getCell(0).getStringCellValue().trim(), dateFormatter);

        String nombreMateriaCompleto = row.getCell(1).getStringCellValue().trim();
        String codigo = nombreMateriaCompleto.substring(nombreMateriaCompleto.indexOf("(") + 1, nombreMateriaCompleto.indexOf(")")).trim();
        String nombreMateria = nombreMateriaCompleto.substring(0, nombreMateriaCompleto.indexOf("(")).trim();

        String tipo = row.getCell(2).getStringCellValue().trim();
        Double nota = ExcelProcessingUtils.extraerNota(row.getCell(3));
        String resultado = row.getCell(4).getStringCellValue().trim();

        return new DatosFila(nombreMateria, codigo, fecha, tipo, nota, resultado);
    }

    private DatosFila extraerFormatoMateriaFecha(Row row, DateTimeFormatter dateFormatter) {
        String nombreMateriaCompleto = row.getCell(0).getStringCellValue().trim();
        String codigo = nombreMateriaCompleto.substring(nombreMateriaCompleto.indexOf("(") + 1, nombreMateriaCompleto.indexOf(")")).trim();
        String nombreMateria = nombreMateriaCompleto.substring(0, nombreMateriaCompleto.indexOf("(")).trim();

        LocalDate fecha = LocalDate.parse(row.getCell(1).getStringCellValue().trim(), dateFormatter);
        String tipo = row.getCell(2).getStringCellValue().trim();
        Double nota = ExcelProcessingUtils.extraerNota(row.getCell(3));
        String resultado = row.getCell(4).getStringCellValue().trim();

        return new DatosFila(nombreMateria, codigo, fecha, tipo, nota, resultado);
    }

    // Métodos para PDF (adaptados)

    @Override
    public HistoriaAcademica procesarArchivoPDF(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        String pdfContent = leerContenidoPDF(file);

        PlanDeEstudio plan = obtenerPlanDeEstudio(codigoPlan);
        Map<String, Materia> materiasDelPlanMap = obtenerMaterialesParaValidacion(codigoPlan);

        List<DatosFila> datosExtraidos = extraerDatosDePDF(pdfContent);

        validarCoincidenciaDelPlan(datosExtraidos, materiasDelPlanMap, codigoPlan);

        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        HistoriaAcademica historia = obtenerOCrearHistoria(estudiante, plan);

        procesarDatos(datosExtraidos, historia, materiasDelPlanMap);

        return historia;
    }

    @Override
    public HistoriaAcademica procesarArchivoPDFActualizacion(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        String pdfContent = leerContenidoPDF(file);

        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        HistoriaAcademica historia = historiaRepo.findByEstudiante(estudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Historia no encontrada para actualización"));

        Map<String, Materia> materiasMap = obtenerMaterialesParaValidacion(codigoPlan);

        List<DatosFila> datosExtraidos = extraerDatosDePDF(pdfContent);
        validarCoincidenciaDelPlan(datosExtraidos, materiasMap, codigoPlan);

        procesarDatosConChequeo(datosExtraidos, historia, materiasMap);

        historia.setEstado("ACTIVA");
        historia = historiaRepo.save(historia);

        return historia;
    }

    private String leerContenidoPDF(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private List<DatosFila> extraerDatosDePDF(String pdfContent) {
        List<DatosFila> datos = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        pdfContent = limpiarPdfRaw(pdfContent);

        // Regex actualizada para incluir materias con mayúsculas, minúsculas, guiones y acentos
        Pattern pattern = Pattern.compile(
                "([A-ZÁÉÍÓÚÜÑa-záéíóúüñ0-9\\s\\.\\-,]+?)\\s*\\(([A-Za-z0-9-]{5,9})\\)\\s+(\\d{2}/\\d{2}/\\d{4})\\s+(Promocion|Regularidad|Examen|Equivalencia)\\s+(?:(\\d+[\\.,]?\\d*)\\s+)?(Aprobado|Promocionado|Reprobado|Ausente)"
        );

        Matcher matcher = pattern.matcher(pdfContent);

        while (matcher.find()) {
            try {
                String nombreMateria = matcher.group(1).trim().toUpperCase();
                String codigo = matcher.group(2).trim();
                LocalDate fecha = LocalDate.parse(matcher.group(3).trim(), dateFormatter);
                String tipo = matcher.group(4).trim();
                String notaStr = matcher.group(5);
                String resultado = matcher.group(6).trim();

                Double nota = null;
                if (notaStr != null && notaStr.matches("[\\d\\.,]+")) {
                    nota = Double.parseDouble(notaStr.replace(',', '.'));
                }

                datos.add(new DatosFila(nombreMateria, codigo, fecha, tipo, nota, resultado));

            } catch (Exception e) {
                log.warn("⚠️ Error al parsear línea: '" + matcher.group(0) + "'. Causa: " + e.getMessage());
            }
        }

        return datos;
    }


    public static String limpiarPdfRaw(String pdfContent) {
        if (pdfContent == null || pdfContent.isEmpty()) {
            return "";
        }

        // Borra todas las líneas que tengan "HISTORIA ACADÉMICA", "Alumno:" o "Propuesta:" en cualquier parte
        pdfContent = pdfContent.replaceAll("(?m)^.*(HISTORIA ACADÉMICA|Alumno:|Propuesta:).*(\\r?\\n)?", "");

        // Eliminar paginación + fecha + hora tipo "1 de 4 08/07/2025 22:32:09"
        pdfContent = pdfContent.replaceAll("\\d+ de \\d+ \\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}", "");

        // 1. Limpiar "Actividad Fecha Tipo Nota Resultado"
        pdfContent = pdfContent.replaceAll("Actividad\\s+Fecha\\s+Tipo\\s+Nota\\s+Resultado", "");

        // 2. Limpiar patrones como "Derecho Procesal Penal (MT2104019) 25/03/2025 En curso"
        // IMPORTANTE: Hacer esto ANTES de reemplazar saltos de línea
        pdfContent = pdfContent.replaceAll("(?m)^([A-ZÁÉÍÓÚÜÑa-záéíóúüñ0-9\\s\\.\\-,]+?)\\s*\\((\\w{9,})\\)\\s+(\\d{2}/\\d{2}/\\d{4})\\s+En\\s+curso\\s*$", "");

        // Reemplazar saltos de línea, retornos de carro y form feeds por espacio simple
        pdfContent = pdfContent.replaceAll("[\\r\\n\\f]+", " ");

        // Unificar múltiples espacios en uno solo
        pdfContent = pdfContent.replaceAll("\\s{2,}", " ");

        // Limpiar espacios al inicio y final
        return pdfContent.trim();
    }
}