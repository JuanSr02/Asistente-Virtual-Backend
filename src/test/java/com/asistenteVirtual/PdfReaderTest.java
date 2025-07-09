package com.asistenteVirtual;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PdfReaderTest {

    private static final String PDF_FILE_PATH = "C:/Users/juan_/Downloads/historia_academica.pdf";

    public static void main(String[] args) {
        System.out.println("Iniciando lectura y parseo del PDF: " + PDF_FILE_PATH);

        try {
            File pdfFile = new File(PDF_FILE_PATH);
            if (!pdfFile.exists()) {
                System.err.println("Error: El archivo PDF no se encontró en la ruta especificada.");
                return;
            }

            String pdfContent = readPdfContent(pdfFile);
            List<DatosFila> extractedData = extractDataFromPdfContent(pdfContent);

            System.out.println("\n--- Datos Extraídos del PDF ---");
            if (extractedData.isEmpty()) {
                System.out.println("No se extrajeron datos. Revisá el patrón regex o el contenido del PDF.");
            } else {
                for (int i = 0; i < extractedData.size(); i++) {
                    DatosFila data = extractedData.get(i);
                    System.out.printf("Registro %d: Materia: '%s' (Código: '%s'), Fecha: %s, Tipo: '%s', Nota: %s, Resultado: '%s'%n",
                            i + 1, data.nombreMateria(), data.codigo(), data.fecha(), data.tipo(),
                            (data.nota() != null ? String.format("%.2f", data.nota()) : "N/A"), data.resultado());
                }
            }
            System.out.println("--- Fin de Datos Extraídos ---");

        } catch (IOException e) {
            System.err.println("Error al leer el archivo PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static String readPdfContent(File pdfFile) throws IOException {
        try (PDDocument document = PDDocument.load(pdfFile)) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        }
    }

    private static List<DatosFila> extractDataFromPdfContent(String pdfContent) {
        List<DatosFila> datos = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        pdfContent = limpiarPdfRaw(pdfContent);

        System.out.println("--- CONTENIDO PDF RAW ---");
        System.out.println(pdfContent.substring(0, Math.min(7000, pdfContent.length()))); // Para evitar error si es menos de 5000 chars

        // 🔹 Regex de extracción
        Pattern pattern = Pattern.compile(
                "([A-ZÁÉÍÓÚÜÑ0-9\\s\\.\\-]+?)\\s*\\((03MA\\w{5})\\)\\s+(\\d{2}/\\d{2}/\\d{4})\\s+(Promocion|Regularidad|Examen)\\s+([\\d\\.,]+)?\\s*(Aprobado|Promocionado|Reprobado|Ausente)"
        );

        Matcher matcher = pattern.matcher(pdfContent);

        while (matcher.find()) {
            try {
                String nombreMateria = matcher.group(1).trim();
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
                System.err.println("⚠️ Error al parsear línea: '" + matcher.group(0) + "'. Causa: " + e.getMessage());
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

        // Reemplazar saltos de línea, retornos de carro y form feeds por espacio simple
        pdfContent = pdfContent.replaceAll("[\\r\\n\\f]+", " ");

        // Unificar múltiples espacios en uno solo
        pdfContent = pdfContent.replaceAll("\\s{2,}", " ");

        // Limpiar espacios al inicio y final
        return pdfContent.trim();
    }


    public record DatosFila(
            String nombreMateria,
            String codigo,
            LocalDate fecha,
            String tipo,
            Double nota,
            String resultado
    ) {
    }
}
