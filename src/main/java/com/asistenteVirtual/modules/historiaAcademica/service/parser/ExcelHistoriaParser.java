package com.asistenteVirtual.modules.historiaAcademica.service.parser;

import com.asistenteVirtual.common.utils.ExcelHelper;
import com.asistenteVirtual.modules.historiaAcademica.dto.DatosFila;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ExcelHistoriaParser implements HistoriaFileParser {

    private static final int ROW_START_DATA = 6; // Según tu lógica original, los datos empiezan en fila 6
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public List<DatosFila> parse(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            return extraerDatos(sheet);
        }
    }

    private List<DatosFila> extraerDatos(Sheet sheet) {
        List<DatosFila> datos = new ArrayList<>();
        int lastRow = ExcelHelper.obtenerUltimaFilaConDatos(sheet);

        for (int i = ROW_START_DATA; i <= lastRow; i++) {
            Row row = sheet.getRow(i);
            if (ExcelHelper.isEmptyRow(row)) continue;

            try {
                DatosFila fila = procesarFila(row);
                if (fila != null) {
                    datos.add(fila);
                }
            } catch (Exception e) {
                log.warn("Error parseando fila Excel {}: {}. Se omite.", i, e.getMessage());
            }
        }
        return datos;
    }

    private DatosFila procesarFila(Row row) {
        String primerCelda = ExcelHelper.extractCellValue(row.getCell(0));

        // Lógica original: Determinar formato basado en si la primera celda es fecha
        if (esFecha(primerCelda)) {
            return extraerFormatoFechaPrimero(row);
        } else {
            return extraerFormatoMateriaPrimero(row);
        }
    }

    private boolean esFecha(String valor) {
        return valor.matches("\\d{2}/\\d{2}/\\d{4}");
    }

    private DatosFila extraerFormatoFechaPrimero(Row row) {
        // Col 0: Fecha | Col 1: Materia (Codigo) | Col 2: Tipo | Col 3: Nota | Col 4: Resultado
        LocalDate fecha = LocalDate.parse(ExcelHelper.extractCellValue(row.getCell(0)), DATE_FORMATTER);
        String materiaCompleta = ExcelHelper.extractCellValue(row.getCell(1));
        
        return construirDatosFila(
                materiaCompleta,
                fecha,
                ExcelHelper.extractCellValue(row.getCell(2)), // Tipo
                ExcelHelper.extraerNota(row.getCell(3)),      // Nota
                ExcelHelper.extractCellValue(row.getCell(4))  // Resultado
        );
    }

    private DatosFila extraerFormatoMateriaPrimero(Row row) {
        // Col 0: Materia (Codigo) | Col 1: Fecha | Col 2: Tipo | Col 3: Nota | Col 4: Resultado
        String materiaCompleta = ExcelHelper.extractCellValue(row.getCell(0));
        LocalDate fecha = LocalDate.parse(ExcelHelper.extractCellValue(row.getCell(1)), DATE_FORMATTER);

        return construirDatosFila(
                materiaCompleta,
                fecha,
                ExcelHelper.extractCellValue(row.getCell(2)),
                ExcelHelper.extraerNota(row.getCell(3)),
                ExcelHelper.extractCellValue(row.getCell(4))
        );
    }

    private DatosFila construirDatosFila(String materiaCompleta, LocalDate fecha, String tipo, Double nota, String resultado) {
        String nombre = materiaCompleta;
        String codigo = "";

        // Parsear "Nombre Materia (CODIGO)"
        if (materiaCompleta.contains("(") && materiaCompleta.contains(")")) {
            nombre = materiaCompleta.substring(0, materiaCompleta.indexOf("(")).trim();
            codigo = materiaCompleta.substring(materiaCompleta.indexOf("(") + 1, materiaCompleta.indexOf(")")).trim();
        }

        return new DatosFila(nombre, codigo, fecha, tipo, nota, resultado);
    }
}