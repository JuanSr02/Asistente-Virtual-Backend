package com.recommendationSys.Sistema_Recomendador_Finales.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasGeneralesDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasMateriaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.MateriaRankingDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.EstadisticasMateria;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstadisticasMateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExamenRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExperienciaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final ExperienciaRepository experienciaRepo;
    private final ExamenRepository examenRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;
    private final MateriaRepository materiaRepo;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 2 * * ?") // Actualiza diariamente a las 2 AM
    @Transactional
    public void actualizarEstadisticas() {
        List<Materia> materias = examenRepo.findDistinctMaterias();

        materias.forEach(materia -> {
            EstadisticasMateria stats = obtenerEstadisticasMateria(materia);
            estadisticasRepo.save(stats);
        });
    }

    public EstadisticasMateria calcularEstadisticasPorMateria(String codigoMateria){
        Materia materia = materiaRepo.findById(codigoMateria).orElseThrow();
        return obtenerEstadisticasMateria(materia);
    }

    public EstadisticasMateria obtenerEstadisticasMateria(Materia materia) {
        List<Examen> examenes = examenRepo.findByMateriaWithJoins(materia);
        List<Experiencia> experiencias = experienciaRepo.findByMateriaWithJoins(materia);
        EstadisticasMateria stats = new EstadisticasMateria();
        stats.setCodigoMateria(materia.getCodigo());
        stats.setNombreMateria(materia.getNombre());

        // Cálculos básicos
        stats.setTotalRendidos(examenes.size());
        stats.setAprobados(calcularAprobados(examenes));
        stats.setReprobados(examenes.size() - stats.getAprobados());
        stats.setPromedioNotas(calcularPromedioNotas(examenes));

        // Cálculos de experiencia
        stats.setPromedioDiasEstudio(calcularPromedioDiasEstudio(experiencias));
        stats.setPromedioHorasDiarias(calcularPromedioHorasDiarias(experiencias));

        // Distribuciones
        stats.setDistribucionDificultad(calcularDistribucionDificultad(experiencias));
        stats.setDistribucionModalidad(calcularDistribucionModalidad(experiencias));
        stats.setDistribucionRecursos(calcularDistribucionRecursos(experiencias));

        stats.setUltimaActualizacion(LocalDateTime.now());

        return stats;
    }

    // Métodos auxiliares de cálculo
    private Integer calcularAprobados(List<Examen> examenes) {
        return Math.toIntExact(examenes.stream().filter(e -> e.getNota() >= 4).count());
    }

    private double calcularPromedioNotas(List<Examen> examenes) {
        return examenes.stream()
                .mapToDouble(Examen::getNota)
                .average()
                .orElse(0.0);
    }

    private String calcularDistribucionModalidad(List<Experiencia> experiencias) {
        Map<String, Long> distribucion = experiencias.stream()
                .collect(Collectors.groupingBy(
                        Experiencia::getModalidad,
                        Collectors.counting()
                ));

        try {
            return objectMapper.writeValueAsString(distribucion);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    private double calcularPromedioDiasEstudio(List<Experiencia> experiencias) {
        return experiencias.stream()
                .filter(e -> e.getDiasEstudio() != null)
                .mapToInt(Experiencia::getDiasEstudio)
                .average()
                .orElse(0.0);
    }

    private double calcularPromedioHorasDiarias(List<Experiencia> experiencias) {
        return experiencias.stream()
                .filter(e -> e.getHorasDiarias() != null)
                .mapToInt(Experiencia::getHorasDiarias)
                .average()
                .orElse(0.0);
    }

    private String calcularDistribucionDificultad(List<Experiencia> experiencias) {
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
            return "{}";
        }
    }

    private String calcularDistribucionRecursos(List<Experiencia> experiencias) {
        // Suponiendo que recursos es un string separado por comas
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
            return "{}";
        }
    }

    public EstadisticasGeneralesDTO obtenerEstadisticasGenerales() {
        List<Object[]> topAprobadas = examenRepo.findTop5MateriasAprobadas();
        List<Object[]> topReprobadas = examenRepo.findTop5MateriasReprobadas();

        EstadisticasGeneralesDTO dto = new EstadisticasGeneralesDTO();

        // Totales generales
        dto.setTotalMaterias((int) materiaRepo.count());
        dto.setTotalExamenesRendidos((int) examenRepo.count());

        // Porcentaje general de aprobados
        long totalAprobados = examenRepo.countByNotaGreaterThanEqual(4.0);
        dto.setPorcentajeAprobadosGeneral(
                (double) totalAprobados / dto.getTotalExamenesRendidos() * 100
        );

        // Top 5 materias
        dto.setTop5Aprobadas(mapToMateriaRankingDTO(topAprobadas));
        dto.setTop5Reprobadas(mapToMateriaRankingDTO(topReprobadas));

        // Promedios por materia
        dto.setPromedioNotasPorMateria(
                estadisticasRepo.findAll().stream()
                        .collect(Collectors.toMap(
                                EstadisticasMateria::getNombreMateria,
                                EstadisticasMateria::getPromedioNotas
                        ))
        );

        return dto;
    }

    private List<MateriaRankingDTO> mapToMateriaRankingDTO(List<Object[]> results) {
        return results.stream()
                .map(row -> {
                    MateriaRankingDTO dto = new MateriaRankingDTO();
                    dto.setCodigoMateria((String) row[0]);
                    dto.setNombre((String) row[1]);
                    long total = ((Number) row[2]).longValue();
                    long aprobados = ((Number) row[3]).longValue();
                    dto.setPorcentaje((double) aprobados / total * 100);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public EstadisticasMateriaDTO convertToDTO(EstadisticasMateria stats) {
        EstadisticasMateriaDTO dto = new EstadisticasMateriaDTO();

        dto.setCodigoMateria(stats.getCodigoMateria());
        dto.setNombreMateria(stats.getNombreMateria());
        dto.setTotalRendidos(stats.getTotalRendidos());
        dto.setAprobados(stats.getAprobados());
        dto.setReprobados(stats.getReprobados());
        dto.setPromedioNotas(stats.getPromedioNotas());
        dto.setPromedioDiasEstudio(stats.getPromedioDiasEstudio());
        dto.setPromedioHorasDiarias(stats.getPromedioHorasDiarias());

        // Calcular porcentaje
        dto.setPorcentajeAprobados(
                stats.getTotalRendidos() > 0 ?
                        (double) stats.getAprobados() / stats.getTotalRendidos() * 100 : 0.0
        );

        // Deserializar distribuciones
        try {
            if (stats.getDistribucionDificultad() != null) {
                dto.setDistribucionDificultad(
                        objectMapper.readValue(
                                stats.getDistribucionDificultad(),
                                new TypeReference<Map<Integer, Integer>>() {}
                        )
                );
            }

            if (stats.getDistribucionModalidad() != null) {
                dto.setDistribucionModalidad(
                        objectMapper.readValue(
                                stats.getDistribucionModalidad(),
                                new TypeReference<Map<String, Integer>>() {}
                        )
                );
            }

            if (stats.getDistribucionRecursos() != null) {
                dto.setDistribucionRecursos(
                        objectMapper.readValue(
                                stats.getDistribucionRecursos(),
                                new TypeReference<Map<String, Integer>>() {}
                        )
                );
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error deserializando distribuciones" + e);
        }

        dto.setUltimaActualizacion(
                stats.getUltimaActualizacion().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );

        return dto;
    }
}