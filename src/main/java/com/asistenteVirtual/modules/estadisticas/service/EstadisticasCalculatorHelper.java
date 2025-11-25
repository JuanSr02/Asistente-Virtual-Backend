package com.asistenteVirtual.modules.estadisticas.service;

import com.asistenteVirtual.modules.estadisticas.dto.MateriaRankingResponse;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasMateria;
import com.asistenteVirtual.modules.historiaAcademica.model.Examen;
import com.asistenteVirtual.modules.historiaAcademica.model.Experiencia;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class EstadisticasCalculatorHelper {

    // --- Cálculos Generales ---

    public Map<String, Integer> calcularDistribucionEstudiantesPorCarrera(List<HistoriaAcademica> historias) {
        if (historias == null || historias.isEmpty()) return Collections.emptyMap();

        return historias.stream()
                .map(h -> h.getPlanDeEstudio().getPropuesta())
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.reducing(0, e -> 1, Integer::sum)
                ));
    }

    public Map<String, Integer> calcularDistribucionExamenesPorMateria(List<Examen> examenes) {
        if (examenes == null || examenes.isEmpty()) {
            return Collections.emptyMap();
        }

        // Normalizador: Mayúsculas y sin acentos para agrupar correctamente
        Function<String, String> normalizarNombre = nombre ->
                Normalizer.normalize(nombre.toUpperCase(), Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        Map<String, Integer> conteo = examenes.stream()
                .filter(e -> e.getRenglon() != null && e.getRenglon().getMateria() != null)
                .collect(Collectors.groupingBy(
                        e -> normalizarNombre.apply(e.getRenglon().getMateria().getNombre()),
                        Collectors.reducing(0, e -> 1, Integer::sum)
                ));

        // Retornar ordenado por cantidad descendente
        return conteo.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    // --- Cálculos Matemáticos ---

    public Integer calcularAprobados(List<Examen> examenes) {
        if (examenes == null) return 0;
        return (int) examenes.stream()
                .filter(e -> e.getNota() != null && e.getNota() >= 4)
                .count();
    }

    public Double calcularPromedioNotas(List<Examen> examenes) {
        if (examenes == null || examenes.isEmpty()) return 0.0;
        return examenes.stream()
                .filter(e -> e.getNota() != null)
                .mapToDouble(Examen::getNota)
                .average()
                .orElse(0.0);
    }

    public Double calcularPorcentajeGlobal(List<Examen> examenes) {
        if (examenes == null || examenes.isEmpty()) return 0.0;
        long aprobados = examenes.stream()
                .filter(e -> e.getNota() != null && e.getNota() >= 4)
                .count();
        return calcularPorcentaje(aprobados, examenes.size());
    }

    public double calcularPorcentaje(long parcial, long total) {
        return total > 0 ? (double) parcial / total * 100 : 0.0;
    }

    // --- Transformaciones y Rankings ---

    public MateriaRankingResponse crearRankingDTO(String codigoMateria, String nombre, long cantidad, long aprobados) {
        Double porcentaje = calcularPorcentaje(aprobados, cantidad);
        return new MateriaRankingResponse(codigoMateria, nombre, porcentaje);
    }

    public Map<String, Double> obtenerPromediosPorMateria(List<EstadisticasMateria> todasLasEstadisticas) {
        if (todasLasEstadisticas == null) return Collections.emptyMap();
        return todasLasEstadisticas.stream()
                .collect(Collectors.toMap(
                        EstadisticasMateria::getNombreMateria,
                        EstadisticasMateria::getPromedioNotas,
                        (existente, nuevo) -> existente // En caso de conflicto, mantiene el primero
                ));
    }

    public List<MateriaRankingResponse> mapToMateriaRankingResponse(List<Object[]> results) {
        if (results == null) return Collections.emptyList();
        return results.stream().map(this::convertirFilaARanking).toList();
    }

    private MateriaRankingResponse convertirFilaARanking(Object[] row) {
        String codigo = (String) row[0];
        String nombre = (String) row[1];

        // Regla de negocio específica heredada
        if ("MA158".equals(codigo)) {
            nombre += " (TEC. WEB.)";
        }

        long total = ((Number) row[2]).longValue();
        long aprobados = ((Number) row[3]).longValue();

        return new MateriaRankingResponse(codigo, nombre, calcularPorcentaje(aprobados, total));
    }

    // --- Métodos de Experiencia (Feedback) ---

    public Double calcularPromedioDiasEstudio(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) return 0.0;
        // Solo consideramos el tiempo de estudio de quienes aprobaron (dato de calidad)
        return experiencias.stream()
                .filter(e -> e.getDiasEstudio() != null && esExamenAprobado(e))
                .mapToInt(Experiencia::getDiasEstudio)
                .average().orElse(0.0);
    }

    public Double calcularPromedioHorasDiarias(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) return 0.0;
        return experiencias.stream()
                .filter(e -> e.getHorasDiarias() != null && esExamenAprobado(e))
                .mapToInt(Experiencia::getHorasDiarias)
                .average().orElse(0.0);
    }

    public Double calcularPromedioDificultad(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) return 0.0;
        // La dificultad es subjetiva de todos, aprobados o no
        return experiencias.stream()
                .filter(e -> e.getDificultad() != null)
                .mapToInt(Experiencia::getDificultad)
                .average().orElse(0.0);
    }

    private boolean esExamenAprobado(Experiencia e) {
        return e.getExamen() != null && e.getExamen().getNota() != null && e.getExamen().getNota() >= 4;
    }

    // --- Distribuciones (Retornan Mapas, NO JSON Strings) ---

    public Map<Integer, Long> calcularDistribucionDificultadMap(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) return Collections.emptyMap();

        Map<Integer, Long> dist = experiencias.stream()
                .filter(e -> e.getDificultad() != null)
                .collect(Collectors.groupingBy(Experiencia::getDificultad, Collectors.counting()));

        // Rellenar huecos del 1 al 10
        for (int i = 1; i <= 10; i++) {
            dist.putIfAbsent(i, 0L);
        }
        return dist;
    }

    public Map<String, Long> calcularDistribucionModalidadMap(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) return Collections.emptyMap();
        
        return experiencias.stream()
                .filter(e -> e.getModalidad() != null && !e.getModalidad().isEmpty())
                .collect(Collectors.groupingBy(Experiencia::getModalidad, Collectors.counting()));
    }

    public Map<String, Long> calcularDistribucionRecursosMap(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) return Collections.emptyMap();

        // Split por comas, limpiar espacios y contar
        return experiencias.stream()
                .filter(e -> e.getRecursos() != null && !e.getRecursos().isEmpty())
                .flatMap(e -> Arrays.stream(e.getRecursos().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty()))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }
}