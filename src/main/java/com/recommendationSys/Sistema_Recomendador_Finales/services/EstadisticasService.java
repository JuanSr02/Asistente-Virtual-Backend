package com.recommendationSys.Sistema_Recomendador_Finales.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommendationSys.Sistema_Recomendador_Finales.model.EstadisticasMateria;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstadisticasMateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExamenRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.ExperienciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstadisticasService {

    private final ExperienciaRepository experienciaRepo;
    private final ExamenRepository examenRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;
    private final ObjectMapper objectMapper;

    @Scheduled(cron = "0 0 2 * * ?") // Actualiza diariamente a las 2 AM
    @Transactional
    public void actualizarEstadisticas() {
        List<Materia> materias = examenRepo.findDistinctMaterias();

        materias.forEach(materia -> {
            EstadisticasMateria stats = calcularEstadisticas(materia);
            estadisticasRepo.save(stats);
        });
    }

    private EstadisticasMateria calcularEstadisticas(Materia materia) {
        List<Experiencia> experiencias = experienciaRepo.findByMateria(materia);
        List<Examen> examenes = examenRepo.findByMateria(materia);

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
    private long calcularAprobados(List<Examen> examenes) {
        return examenes.stream().filter(e -> e.getNota() >= 4).count();
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

    // ... otros métodos de cálculo
}