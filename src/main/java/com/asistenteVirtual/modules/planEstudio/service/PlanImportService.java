package com.asistenteVirtual.modules.planEstudio.service;

import com.asistenteVirtual.common.exceptions.PlanEstudioValidationException;
import com.asistenteVirtual.common.utils.ExcelHelper;
import com.asistenteVirtual.modules.planEstudio.model.Correlativa;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.CorrelativaRepository;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.asistenteVirtual.modules.planEstudio.repository.PlanDeEstudioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanImportService {

    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final CorrelativaRepository correlativaRepo;

    // Constantes para ubicar datos en el Excel según tu formato actual
    private static final int ROW_INDEX_PLAN_INFO = 1;
    private static final int ROW_INDEX_MATERIAS_START = 4;
    private static final int COL_INDEX_MATERIA_NOMBRE = 0;
    private static final int COL_INDEX_MATERIA_CODIGO = 1;
    private static final int COL_INDEX_CORRELATIVAS = 5;

    @Transactional
    public PlanDeEstudio procesarArchivoExcel(MultipartFile file) throws IOException {
        validarArchivo(file);

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            // 1. Procesar y guardar el Plan
            PlanDeEstudio plan = procesarCabeceraPlan(sheet);

            // Validar si ya existe
            if (planRepo.existsById(plan.getCodigo())) {
                throw new PlanEstudioValidationException("El plan de estudios con código " + plan.getCodigo() + " ya está cargado.");
            }
            plan = planRepo.save(plan);

            // 2. Procesar y guardar Materias
            Map<String, Materia> mapaMaterias = procesarMaterias(sheet, plan);

            // 3. Procesar y guardar Correlativas (requiere que las materias ya existan o estén en memoria)
            procesarCorrelativas(sheet, mapaMaterias, plan);

            return plan;
        }
    }

    private void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("El archivo no puede estar vacío");
        }
        String fileName = file.getOriginalFilename();
        if (fileName != null && !fileName.endsWith(".xlsx") && !fileName.endsWith(".xls")) {
            throw new PlanEstudioValidationException("Formato de archivo no válido. Solo se permiten .xls y .xlsx");
        }
    }

    private PlanDeEstudio procesarCabeceraPlan(Sheet sheet) {
        Row row = sheet.getRow(ROW_INDEX_PLAN_INFO);
        if (row == null) {
            throw new PlanEstudioValidationException("No se encontró la fila de información del plan (Fila 2)");
        }

        String propuestaRaw = ExcelHelper.extractCellValue(row.getCell(0));
        String codigo = ExcelHelper.extractCellValue(row.getCell(1));

        if (propuestaRaw.isBlank() || codigo.isBlank()) {
            throw new PlanEstudioValidationException("El nombre de la propuesta o el código del plan están vacíos.");
        }

        // Limpieza del nombre (ej: "LICENCIATURA (2010)" -> "LICENCIATURA")
        String propuesta = propuestaRaw.contains("(")
                ? propuestaRaw.substring(0, propuestaRaw.indexOf("(")).trim()
                : propuestaRaw.trim();

        return PlanDeEstudio.builder()
                .codigo(codigo)
                .propuesta(propuesta)
                .build();
    }

    private Map<String, Materia> procesarMaterias(Sheet sheet, PlanDeEstudio plan) {
        List<Materia> materiasParaGuardar = new ArrayList<>();
        int lastRow = ExcelHelper.obtenerUltimaFilaConDatos(sheet);

        for (int i = ROW_INDEX_MATERIAS_START; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (ExcelHelper.isEmptyRow(row)) continue;

            String nombre = ExcelHelper.extractCellValue(row.getCell(COL_INDEX_MATERIA_NOMBRE));
            String codigo = ExcelHelper.extractCellValue(row.getCell(COL_INDEX_MATERIA_CODIGO));

            if (nombre.isBlank() || codigo.isBlank()) {
                log.warn("Fila {} ignorada: Falta nombre o código de materia", i + 1);
                continue;
            }

            Materia materia = Materia.builder()
                    .codigo(codigo)
                    .nombre(nombre)
                    .planDeEstudio(plan)
                    .build();

            materiasParaGuardar.add(materia);
        }

        // Guardado masivo (Batch Insert)
        List<Materia> guardadas = materiaRepo.saveAll(materiasParaGuardar);

        // Retornamos un mapa para búsqueda rápida en el paso de correlativas
        return guardadas.stream()
                .collect(Collectors.toMap(Materia::getCodigo, m -> m));
    }

    private void procesarCorrelativas(Sheet sheet, Map<String, Materia> mapaMaterias, PlanDeEstudio plan) {
        List<Correlativa> correlativasParaGuardar = new ArrayList<>();
        int lastRow = ExcelHelper.obtenerUltimaFilaConDatos(sheet);

        for (int i = ROW_INDEX_MATERIAS_START; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (ExcelHelper.isEmptyRow(row)) continue;

            String codigoMateria = ExcelHelper.extractCellValue(row.getCell(COL_INDEX_MATERIA_CODIGO));
            String correlativasStr = ExcelHelper.extractCellValue(row.getCell(COL_INDEX_CORRELATIVAS));

            Materia materiaPrincipal = mapaMaterias.get(codigoMateria);

            if (materiaPrincipal != null && !correlativasStr.isBlank() && !"No tiene".equalsIgnoreCase(correlativasStr)) {
                // Separar por guión "-", limpiar espacios y buscar la materia correlativa
                Arrays.stream(correlativasStr.split("-"))
                        .map(String::trim)
                        .map(mapaMaterias::get) // Buscar en el mapa
                        .filter(Objects::nonNull) // Ignorar si no existe (o lanzar error si prefieres estricto)
                        .forEach(materiaCorrelativa -> {
                            Correlativa correlativa = new Correlativa();
                            correlativa.setMateria(materiaPrincipal);
                            correlativa.setCorrelativaCodigo(materiaCorrelativa); // Usando tu relación actual
                            // Nota: Si Correlativa tiene ID compuesto, asegúrate de setearlo o que JPA lo maneje
                            correlativasParaGuardar.add(correlativa);
                        });
            }
        }

        if (!correlativasParaGuardar.isEmpty()) {
            correlativaRepo.saveAll(correlativasParaGuardar);
        }
    }
}