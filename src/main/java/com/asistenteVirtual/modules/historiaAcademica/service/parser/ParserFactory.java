package com.asistenteVirtual.modules.historiaAcademica.service.parser;

import com.asistenteVirtual.common.exceptions.UnsupportedFileTypeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ParserFactory {

    private final ExcelHistoriaParser excelParser;
    private final PdfHistoriaParser pdfParser;

    public HistoriaFileParser getParser(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) throw new UnsupportedFileTypeException("Nombre de archivo inválido");

        String ext = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        return switch (ext) {
            case "xlsx", "xls" -> excelParser;
            case "pdf" -> pdfParser;
            default -> throw new UnsupportedFileTypeException("Formato no soportado: " + ext);
        };
    }
}