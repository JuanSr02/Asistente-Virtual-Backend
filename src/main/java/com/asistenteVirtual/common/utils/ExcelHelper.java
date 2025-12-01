package com.asistenteVirtual.common.utils;

import lombok.experimental.UtilityClass;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Utilidades para el procesamiento de archivos Excel.
 * Uso de @UtilityClass de Lombok para hacerla final y con constructor privado automáticamente.
 */
@UtilityClass
public class ExcelHelper {

    public boolean isEmptyRow(Row row) {
        if (row == null) return true;

        // Optimización: Usamos stream o un loop rápido. El loop es más eficiente para POI.
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public String extractCellValue(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> formatNumericValue(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA -> handleFormulaCell(cell);
            default -> throw new IllegalArgumentException(
                    "Tipo de celda no soportado: " + cell.getCellType()
            );
        };
    }

    public int obtenerUltimaFilaConDatos(Sheet sheet) {
        // Recorremos desde el final hacia arriba para ser más eficientes
        for (int i = sheet.getLastRowNum(); i >= 0; i--) {
            if (!isEmptyRow(sheet.getRow(i))) {
                return i;
            }
        }
        return 0;
    }

    public Double extraerNota(Cell cell) {
        String valor = extractCellValue(cell);
        if (valor.isBlank()) return null;

        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return null; // O lanzar excepción si el dato corrupto no es tolerable
        }
    }

    // --- Métodos privados ---

    private String formatNumericValue(double numericValue) {
        // Usamos Math.rint para asegurar redondeo correcto si es casi entero
        if (numericValue == (long) numericValue) {
            return String.valueOf((long) numericValue);
        }
        return String.valueOf(numericValue).trim();
    }

    private String handleFormulaCell(Cell cell) {
        try {
            return switch (cell.getCachedFormulaResultType()) {
                case STRING -> cell.getStringCellValue().trim();
                case NUMERIC -> formatNumericValue(cell.getNumericCellValue());
                case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
                default -> "";
            };
        } catch (Exception e) {
            return ""; // Fallback seguro si la fórmula falla
        }
    }
}