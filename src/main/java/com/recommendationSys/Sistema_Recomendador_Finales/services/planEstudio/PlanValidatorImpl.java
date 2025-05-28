package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.PlanEstudioValidationException;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.services.ExcelProcessingUtils;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@Service
public class PlanValidatorImpl implements PlanValidator {

    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("xls", "xlsx");

    @Override
    public void validarArchivo(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo no puede estar vacío");
        }

        String fileName = file.getOriginalFilename();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            throw new PlanEstudioValidationException(
                    "Solo se permiten archivos Excel (.xls, .xlsx)");
        }
    }

    @Override
    public void validarFilaPlan(Row planRow) {
        if (planRow == null || planRow.getCell(0) == null) {
            throw new PlanEstudioValidationException(
                    "No se encontró la información del plan de estudios en la primera fila");
        }

        String propuesta = ExcelProcessingUtils.extractCellValue(planRow.getCell(0));
        if (propuesta == null || propuesta.trim().isEmpty()) {
            throw new PlanEstudioValidationException(
                    "La propuesta del plan de estudios no puede estar vacía");
        }

        String codigoPlan = ExcelProcessingUtils.extractCellValue(planRow.getCell(1));
        if (codigoPlan == null || codigoPlan.trim().isEmpty()) {
            throw new ResourceNotFoundException(
                    "No se pudo encontrar el código del plan de estudios");
        }
    }

    @Override
    public void validarFilaMateria(Row row, int rowNum) {
        if (row.getCell(0) == null || row.getCell(1) == null || row.getCell(5) == null) {
            throw new PlanEstudioValidationException(
                    String.format("Fila %d: Faltan datos requeridos para la materia", rowNum + 1));
        }

        String codigoMateria = ExcelProcessingUtils.extractCellValue(row.getCell(1));
        if (codigoMateria == null || codigoMateria.trim().isEmpty()) {
            throw new PlanEstudioValidationException(
                    String.format("Fila %d: El código de la materia no puede estar vacío", rowNum + 1));
        }
    }
}