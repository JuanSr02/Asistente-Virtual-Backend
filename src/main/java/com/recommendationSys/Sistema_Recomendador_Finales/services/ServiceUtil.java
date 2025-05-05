package com.recommendationSys.Sistema_Recomendador_Finales.services;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public class ServiceUtil {

    // Metodo auxiliar para verificar filas vacías
    public static boolean isEmptyRow(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    public static String checkCell(Cell cell){
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int)cell.getNumericCellValue());
            case BLANK:
                break;
            default:
                throw new IllegalArgumentException("Tipo de celda no soportado: " + cell.getCellType());
        }
        return "";
    }

}
