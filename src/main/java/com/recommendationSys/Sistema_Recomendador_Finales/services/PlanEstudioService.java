package com.recommendationSys.Sistema_Recomendador_Finales.services;

import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.PlanEstudioException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Correlativa;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.CorrelativaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.PlanDeEstudioRepository;
import jakarta.transaction.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class PlanEstudioService {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("xls", "xlsx");

    private final PlanDeEstudioRepository planRepo;
    private final MateriaRepository materiaRepo;
    private final CorrelativaRepository correlativaRepo;

    public PlanEstudioService(PlanDeEstudioRepository planRepo,
                              MateriaRepository materiaRepo,
                              CorrelativaRepository correlativaRepo) {
        this.planRepo = planRepo;
        this.materiaRepo = materiaRepo;
        this.correlativaRepo = correlativaRepo;
    }

    public void procesarArchivoExcel(MultipartFile file) throws IOException {
        // Validación 1: Archivo no vacío
        if (file == null || file.isEmpty()) {
            throw new PlanEstudioException("El archivo no puede estar vacío", HttpStatus.BAD_REQUEST);
        }

        // Validación 2: Extensión permitida
        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new PlanEstudioException(
                    "Solo se permiten archivos Excel (.xls, .xlsx)",
                    HttpStatus.BAD_REQUEST
            );
        }

        try {
            procesarContenidoPlan(file);
        } catch (IOException e) {
            throw new PlanEstudioException(
                    "Error al leer el archivo Excel: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new PlanEstudioException(
                    "Formato de datos incorrecto en el archivo: " + e.getMessage(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private void procesarContenidoPlan(MultipartFile file) throws IOException {
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        // Validar fila de plan de estudios
        Row planRow = sheet.getRow(1);
        if (planRow == null || planRow.getCell(0) == null) {
            throw new PlanEstudioException(
                    "No se encontró la información del plan de estudios en la primera fila",
                    HttpStatus.BAD_REQUEST
            );
        }

        String propuesta = planRow.getCell(0).getStringCellValue().trim();
        propuesta = propuesta.substring(0,propuesta.indexOf("(")).trim();
        if (propuesta.trim().isEmpty()) {
            throw new PlanEstudioException(
                    "La propuesta del plan de estudios no puede estar vacía",
                    HttpStatus.BAD_REQUEST
            );
        }

        // Validar formato de código de plan
        String codigoPlan = planRow.getCell(1).getStringCellValue().trim();
        if (codigoPlan == null || codigoPlan.trim().isEmpty()) {
            throw new PlanEstudioException(
                    "No se pudo encontrar el código del plan de estudios",
                    HttpStatus.BAD_REQUEST
            );
        }



        // Crear o actualizar plan
        PlanDeEstudio plan = planRepo.findById(codigoPlan)
                .orElse(new PlanDeEstudio());
        plan.setCodigo(codigoPlan);
        plan.setPropuesta(propuesta);
        // Después de guardar el plan, verifica que tiene un código
        planRepo.save(plan);

        int lastRowWithData = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (sheet.getRow(i) != null && !ServiceUtil.isEmptyRow(sheet.getRow(i))) {
                lastRowWithData = i;
            }
        }

        // Procesar materias (empezando desde la fila 4)
        for (int i = 4; i <= lastRowWithData; i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            // Validar celdas requeridas
            if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(5) == null) {
                throw new PlanEstudioException(
                        String.format("Fila %d: Faltan datos requeridos para la materia", i+1),
                        HttpStatus.BAD_REQUEST
                );
            }

            String codigoMateria = ServiceUtil.checkCell(row.getCell(1));
            if (codigoMateria == null || codigoMateria.trim().isEmpty()) {
                throw new PlanEstudioException(
                        String.format("Fila %d: El código de la materia no puede estar vacío", i+1),
                        HttpStatus.BAD_REQUEST
                );
            }

            String nombreMateria = row.getCell(0).getStringCellValue().trim();

            Materia materia = materiaRepo.findById(codigoMateria)
                    .orElse(new Materia());
            materia.setCodigo(codigoMateria);
            materia.setNombre(nombreMateria);
            materia.setPlanDeEstudio(plan);
            materiaRepo.save(materia);

            // Procesar correlativas
            Cell cell = row.getCell(5);
            String correlativasStr = ServiceUtil.checkCell(cell);
            if (!"No tiene".equalsIgnoreCase(correlativasStr)) {
                String[] codigosCorrelativas = correlativasStr.split("-");
                for (String codigoCorrelativa : codigosCorrelativas) {
                    if (!codigoCorrelativa.trim().isEmpty()) {
                        String codigoTrimmed = codigoCorrelativa.trim();
                        if (materiaRepo.existsById(codigoTrimmed)) {
                            Materia materiaCorrelativa = materiaRepo.findById(codigoTrimmed).get();
                            Correlativa correlativa = new Correlativa();
                            correlativa.setMateria(materia);
                            correlativa.setCorrelativa(materiaCorrelativa);
                            correlativa.setPlanDeEstudio(plan);
                            correlativaRepo.save(correlativa);
                        }

                    }
                }
            }
        }
    }




        public void eliminarPlanDeEstudio(String codigoPlan) {
            // Verificación de existencia con mensaje más descriptivo
            if (!planRepo.existsById(codigoPlan)) {
                throw new PlanEstudioException(
                        String.format("El plan con código '%s' no existe", codigoPlan),
                        HttpStatus.NOT_FOUND
                );
            }
            try {
                // Eliminación en cascada automática gracias a JPA
                planRepo.deleteById(codigoPlan);
            } catch (DataIntegrityViolationException e) {
                throw new PlanEstudioException(
                        "No se puede eliminar el plan porque tiene referencias activas",
                        HttpStatus.CONFLICT
                );
            }
        }

}

