package com.asistenteVirtual.modules.historiaAcademica.service.parser;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.asistenteVirtual.modules.historiaAcademica.dto.DatosFila;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class PdfHistoriaParser implements HistoriaFileParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Regex mejorada y compilada una sola vez para rendimiento
    private static final Pattern REGEX_HISTORIA = Pattern.compile(
            "([A-ZÁÉÍÓÚÜÑa-záéíóúüñ0-9\\s\\.\\-,]+?)\\s*\\(([A-Za-z0-9-]{5,9})\\)\\s+(\\d{2}/\\d{2}/\\d{4})\\s+(Promocion|Regularidad|Examen|Equivalencia|AprobRes)\\s+(?:(\\d+[\\.,]?\\d*)\\s+)?(Aprobado|Promocionado|Reprobado|Ausente)"
    );

    @Override
    public List<DatosFila> parse(MultipartFile file) throws IOException {
        String contenidoRaw = extraerTextoPdf(file);
        String contenidoLimpio = limpiarContenidoRaw(contenidoRaw);
        return procesarTextoConRegex(contenidoLimpio);
    }

    private String extraerTextoPdf(MultipartFile file) throws IOException {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private List<DatosFila> procesarTextoConRegex(String contenido) {
        List<DatosFila> datos = new ArrayList<>();
        Matcher matcher = REGEX_HISTORIA.matcher(contenido);

        while (matcher.find()) {
            try {
                String nombreMateria = matcher.group(1).trim().toUpperCase();
                String codigo = matcher.group(2).trim();
                LocalDate fecha = LocalDate.parse(matcher.group(3).trim(), DATE_FORMATTER);
                String tipo = matcher.group(4).trim();
                String notaStr = matcher.group(5);
                String resultado = matcher.group(6).trim();

                Double nota = parsearNota(notaStr);

                datos.add(new DatosFila(nombreMateria, codigo, fecha, tipo, nota, resultado));

            } catch (Exception e) {
                log.warn("⚠️ Error parseando coincidencia PDF: '{}'. Causa: {}", matcher.group(0), e.getMessage());
            }
        }
        return datos;
    }

    private Double parsearNota(String notaStr) {
        if (notaStr != null && notaStr.matches("[\\d\\.,]+")) {
            try {
                return Double.parseDouble(notaStr.replace(',', '.'));
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private String limpiarContenidoRaw(String text) {
        if (text == null || text.isEmpty()) return "";

        // 1. Eliminar cabeceras irrelevantes
        text = text.replaceAll("(?m)^.*(HISTORIA ACADÉMICA|Alumno:|Propuesta:).*(\\r?\\n)?", "");

        // 2. Eliminar paginación y timestamp de impresión
        text = text.replaceAll("\\d+ de \\d+ \\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}", "");

        // 3. Eliminar fila de títulos de tabla
        text = text.replaceAll("Actividad\\s+Fecha\\s+Tipo\\s+Nota\\s+Resultado", "");

        // 4. Eliminar materias "En curso" que rompen el patrón (Importante hacer antes de unir líneas)
        text = text.replaceAll("(?m)^([A-ZÁÉÍÓÚÜÑa-záéíóúüñ0-9\\s\\.\\-,]+?)\\s*\\((\\w{9,})\\)\\s+(\\d{2}/\\d{2}/\\d{4})\\s+En\\s+curso\\s*$", "");

        // 5. Normalizar espacios y saltos de línea para que la regex funcione en flujo continuo
        text = text.replaceAll("[\\r\\n\\f]+", " "); // Saltos a espacio
        text = text.replaceAll("\\s{2,}", " ");       // Múltiples espacios a uno solo

        return text.trim();
    }
}