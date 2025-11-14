package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;
import com.asistenteVirtual.DTOs.MateriaRankingDTO;
import com.asistenteVirtual.model.EstadisticasMateria;
import com.asistenteVirtual.model.Examen;
import com.asistenteVirtual.model.Experiencia;
import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.repository.EstadisticasMateriaRepository;
import com.asistenteVirtual.repository.EstudianteRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class EstadisticasHelper {
    private final ObjectMapper objectMapper;
    private final EstadisticasMateriaRepository estadisticasRepo;
    private final EstudianteRepository estudianteRepository;

    public Long calcularEstudiantes() {
        return estudianteRepository.count();
    }

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

        // 1. Función para normalizar el nombre de la materia:
        //    - Convierte a mayúsculas.
        //    - Elimina acentos/diacríticos (ej: Á -> A, É -> E).
        Function<String, String> normalizarNombre = nombre ->
                Normalizer.normalize(nombre.toUpperCase(), Normalizer.Form.NFD)
                        .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // 2. Agrupación y conteo, aplicando la normalización en la clave
        Map<String, Integer> conteoNoOrdenado = examenes.stream()
                .filter(e -> e.getRenglon() != null && e.getRenglon().getMateria() != null && e.getRenglon().getMateria().getNombre() != null)
                .collect(Collectors.groupingBy(
                        e -> normalizarNombre.apply(e.getRenglon().getMateria().getNombre()), // Clave: Nombre normalizado
                        Collectors.reducing(0, e -> 1, Integer::sum) // Valor: Conteo
                ));

        // 3. Ordenamiento del mapa por el valor (la suma de exámenes) en orden descendente.
        //    Se usa un LinkedHashMap para preservar el orden de inserción (el orden ordenado).
        return conteoNoOrdenado.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // No debería ocurrir duplicados al final
                        LinkedHashMap::new // Importante: usar LinkedHashMap para mantener el orden
                ));
    }


    public Integer calcularAprobados(List<Examen> examenes) {
        if (examenes == null || examenes.isEmpty()) return 0;
        return Math.toIntExact(examenes.stream()
                .filter(e -> e.getNota() != null && e.getNota() >= 4)
                .count());
    }

    public double calcularPromedioNotas(List<Examen> examenes) {
        if (examenes == null || examenes.isEmpty()) return 0.0;
        return examenes.stream()
                .filter(e -> e.getNota() != null)
                .mapToDouble(Examen::getNota)
                .average()
                .orElse(0.0);
    }

    public MateriaRankingDTO calcularMateriaMasRendida(String codigoMateria, String nombre, long cantidad, long aprobados) {
        MateriaRankingDTO rendida = new MateriaRankingDTO();
        rendida.setCodigoMateria(codigoMateria);
        rendida.setNombre(nombre);
        Double porcentaje = calcularPorcentaje(aprobados, cantidad);
        rendida.setPorcentaje(porcentaje);
        return rendida;
    }

    public double calcularPorcentaje(long parcial, long total) {
        return total > 0 ? (double) parcial / total * 100 : 0.0;
    }

    public double calcularPorcentajeAprobados(EstadisticasMateria stats) {
        return calcularPorcentaje(stats.getAprobados(), stats.getTotalRendidos());
    }

    public Map<String, Double> obtenerPromediosPorMateria() {
        return estadisticasRepo.findAll().stream()
                .collect(Collectors.toMap(
                        EstadisticasMateria::getNombreMateria,
                        EstadisticasMateria::getPromedioNotas,
                        (primero, duplicado) -> primero // en caso de duplicado, quedarse con el primero
                ));

    }

    public List<MateriaRankingDTO> mapToMateriaRankingDTO(List<Object[]> results) {
        if (results == null) return Collections.emptyList();

        return results.stream()
                .map(this::mapearFilaARankingDTO)
                .collect(Collectors.toList());
    }

    private MateriaRankingDTO mapearFilaARankingDTO(Object[] row) {
        MateriaRankingDTO dto = new MateriaRankingDTO();
        dto.setCodigoMateria((String) row[0]);
        if (dto.getCodigoMateria().equals("MA158")) {
            dto.setNombre(row[1] + " (TEC. WEB.)");
        } else {
            dto.setNombre((String) row[1]);
        }

        long total = ((Number) row[2]).longValue();
        long aprobados = ((Number) row[3]).longValue();

        dto.setPorcentaje(calcularPorcentaje(aprobados, total));
        return dto;
    }

    public void cargarDistribuciones(EstadisticasMateriaDTO dto, EstadisticasMateria stats)
            throws JsonProcessingException {
        if (stats.getDistribucionDificultad() != null) {
            dto.setDistribucionDificultad(
                    objectMapper.readValue(
                            stats.getDistribucionDificultad(),
                            new TypeReference<Map<Integer, Integer>>() {
                            }
                    ));
        }

        if (stats.getDistribucionModalidad() != null) {
            dto.setDistribucionModalidad(
                    objectMapper.readValue(
                            stats.getDistribucionModalidad(),
                            new TypeReference<Map<String, Integer>>() {
                            }
                    ));
        }

        if (stats.getDistribucionRecursos() != null) {
            dto.setDistribucionRecursos(
                    objectMapper.readValue(
                            stats.getDistribucionRecursos(),
                            new TypeReference<Map<String, Integer>>() {
                            }
                    ));
        }
    }

    public double calcularPromedioDiasEstudio(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) {
            return 0.0;
        }

        return experiencias.stream()
                .filter(e -> e.getDiasEstudio() != null &&
                        e.getExamen() != null &&
                        e.getExamen().getNota() != null &&
                        e.getExamen().getNota() >= 4)
                .mapToInt(Experiencia::getDiasEstudio)
                .average()
                .orElse(0.0);
    }

    public double calcularPromedioHorasDiarias(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) {
            return 0.0;
        }

        return experiencias.stream()
                .filter(e -> e.getHorasDiarias() != null &&
                        e.getExamen() != null &&
                        e.getExamen().getNota() != null &&
                        e.getExamen().getNota() >= 4)
                .mapToInt(Experiencia::getHorasDiarias)
                .average()
                .orElse(0.0);
    }

    public double calcularPromedioDificultad(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) {
            return 0.0;
        }

        return experiencias.stream()
                .filter(e -> e.getDificultad() != null)
                .mapToInt(Experiencia::getDificultad)
                .average()
                .orElse(0.0);
    }

    public String calcularDistribucionDificultad(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) {
            return "{}";
        }

        Map<Integer, Long> distribucion = experiencias.stream()
                .filter(e -> e.getDificultad() != null)
                .collect(Collectors.groupingBy(
                        Experiencia::getDificultad,
                        Collectors.counting()
                ));

        // Rellenar dificultades del 1 al 10 aunque no tengan datos
        for (int i = 1; i <= 10; i++) {
            distribucion.putIfAbsent(i, 0L);
        }

        try {
            return objectMapper.writeValueAsString(distribucion);
        } catch (JsonProcessingException e) {
            log.error("Error serializando distribución de dificultad", e);
            return "{}";
        }
    }

    public String calcularDistribucionModalidad(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) {
            return "{}";
        }

        Map<String, Long> distribucion = experiencias.stream()
                .filter(e -> e.getModalidad() != null && !e.getModalidad().isEmpty())
                .collect(Collectors.groupingBy(
                        Experiencia::getModalidad,
                        Collectors.counting()
                ));

        try {
            return objectMapper.writeValueAsString(distribucion);
        } catch (JsonProcessingException e) {
            log.error("Error serializando distribución de modalidad", e);
            return "{}";
        }
    }

    public String calcularDistribucionRecursos(List<Experiencia> experiencias) {
        if (experiencias == null || experiencias.isEmpty()) {
            return "{}";
        }

        Map<String, Long> distribucion = experiencias.stream()
                .filter(e -> e.getRecursos() != null && !e.getRecursos().isEmpty())
                .flatMap(e -> Arrays.stream(e.getRecursos().split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty()))
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));

        try {
            return objectMapper.writeValueAsString(distribucion);
        } catch (JsonProcessingException e) {
            log.error("Error serializando distribución de recursos", e);
            return "{}";
        }
    }
}
