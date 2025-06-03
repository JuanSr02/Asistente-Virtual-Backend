package com.recommendationSys.Sistema_Recomendador_Finales.services;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Utilidades para el procesamiento de archivos Excel.
 * Proporciona métodos helper para validar y procesar celdas y filas.
 */
public final class ExcelProcessingUtils {

    /**
     * Verifica si una fila está completamente vacía.
     *
     * @param row la fila a verificar
     * @return true si la fila está vacía o es null, false en caso contrario
     */
    public static boolean isEmptyRow(Row row) {
        if (row == null) {
            return true;
        }

        for (int columnIndex = row.getFirstCellNum(); columnIndex < row.getLastCellNum(); columnIndex++) {
            if (!isCellEmpty(row.getCell(columnIndex))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica si una celda individual está vacía.
     *
     * @param cell la celda a verificar
     * @return true si la celda está vacía o es null
     */
    public static boolean isCellEmpty(Cell cell) {
        return cell == null || cell.getCellType() == CellType.BLANK;
    }

    /**
     * Extrae el valor de una celda como String, manejando diferentes tipos de datos.
     *
     * @param cell la celda de la cual extraer el valor
     * @return el valor de la celda como String, o cadena vacía si está vacía
     * @throws IllegalArgumentException si el tipo de celda no es soportado
     */
    public static String extractCellValue(Cell cell) {
        if (isCellEmpty(cell)) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> formatNumericValue(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA -> handleFormulaCell(cell);
            default -> throw new IllegalArgumentException(
                    String.format("Tipo de celda no soportado: %s en posición [%d,%d]",
                            cell.getCellType(), cell.getRowIndex(), cell.getColumnIndex())
            );
        };
    }

    /**
     * Formatea un valor numérico, manejando enteros y decimales apropiadamente.
     */
    private static String formatNumericValue(double numericValue) {
        // Si es un entero, no mostrar decimales
        if (numericValue == Math.floor(numericValue)) {
            return String.valueOf((long) numericValue);
        }
        return String.valueOf(numericValue).trim();
    }

    /**
     * Maneja celdas con fórmulas evaluando su resultado.
     */
    private static String handleFormulaCell(Cell cell) {
        return switch (cell.getCachedFormulaResultType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> formatNumericValue(cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    public static int obtenerUltimaFilaConDatos(Sheet sheet) {
        int lastRow = 0;
        for (int i = 0; i <= sheet.getLastRowNum(); i++) {
            if (!isEmptyRow(sheet.getRow(i))) {
                lastRow = i;
            }
        }
        return lastRow;
    }

    public static Double extraerNota(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) {
            return null;
        }
        try {
            return Double.parseDouble(extractCellValue(cell).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
