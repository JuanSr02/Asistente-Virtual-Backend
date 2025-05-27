package com.recommendationSys.Sistema_Recomendador_Finales.services.estadisticas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasGeneralesDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasMateriaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstadisticasServiceImpl implements EstadisticasCalculator, EstadisticasGeneralCalculator, EstadisticasMapper {
    private final ExperienciaRepository experienciaRepo;
    private final ExamenRepository examenRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;
    private final MateriaRepository materiaRepo;
    private final ObjectMapper objectMapper;
    private final EstadisticasHelper estadisticasHelper;


    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void actualizarEstadisticas() {
        List<Materia> materias = examenRepo.findDistinctMaterias();
        materias.forEach(this::calcularYGuardarEstadisticas);
    }


    public EstadisticasMateria calcularEstadisticasPorMateria(String codigoMateria) {
        Materia materia = materiaRepo.findById(codigoMateria)
                .orElseThrow(() -> new ResourceNotFoundException(codigoMateria));
        return calcularEstadisticas(materia);
    }

    @Override
    public EstadisticasMateria calcularEstadisticas(Materia materia) {
        List<Examen> examenes = examenRepo.findByMateriaWithJoins(materia);
        List<Experiencia> experiencias = experienciaRepo.findByMateriaWithJoins(materia);

        return EstadisticasMateria.builder()
                .codigoMateria(materia.getCodigo())
                .nombreMateria(materia.getNombre())
                .totalRendidos(examenes.size())
                .aprobados(estadisticasHelper.calcularAprobados(examenes))
                .reprobados(examenes.size() - estadisticasHelper.calcularAprobados(examenes))
                .promedioNotas(estadisticasHelper.calcularPromedioNotas(examenes))
                .promedioDiasEstudio(estadisticasHelper.calcularPromedioDiasEstudio(experiencias))
                .promedioHorasDiarias(estadisticasHelper.calcularPromedioHorasDiarias(experiencias))
                .promedioDificultad(estadisticasHelper.calcularPromedioDificultad(experiencias))
                .distribucionDificultad(estadisticasHelper.calcularDistribucionDificultad(experiencias))
                .distribucionModalidad(estadisticasHelper.calcularDistribucionModalidad(experiencias))
                .distribucionRecursos(estadisticasHelper.calcularDistribucionRecursos(experiencias))
                .fechaUltimaActualizacion(LocalDateTime.now())
                .build();
    }

    @Override
    public EstadisticasGeneralesDTO calcularEstadisticasGenerales() {
        List<Object[]> topAprobadas = examenRepo.findTop5MateriasAprobadas();
        List<Object[]> topReprobadas = examenRepo.findTop5MateriasReprobadas();

        long totalExamenes = examenRepo.count();
        long totalAprobados = examenRepo.countByNotaGreaterThanEqual(4.0);

        return EstadisticasGeneralesDTO.builder()
                .totalMaterias((int) materiaRepo.count())
                .totalExamenesRendidos((int) totalExamenes)
                .porcentajeAprobadosGeneral(estadisticasHelper.calcularPorcentaje(totalAprobados, totalExamenes))
                .top5Aprobadas(estadisticasHelper.mapToMateriaRankingDTO(topAprobadas))
                .top5Reprobadas(estadisticasHelper.mapToMateriaRankingDTO(topReprobadas))
                .promedioNotasPorMateria(estadisticasHelper.obtenerPromediosPorMateria())
                .build();
    }

    @Override
    public EstadisticasMateriaDTO convertToDTO(EstadisticasMateria stats) {
        if (stats == null) {
            throw new IllegalArgumentException("Las estadísticas no pueden ser nulas");
        }

        EstadisticasMateriaDTO dto = new EstadisticasMateriaDTO();
        dto.setCodigoMateria(stats.getCodigoMateria());
        dto.setNombreMateria(stats.getNombreMateria());
        dto.setTotalRendidos(stats.getTotalRendidos());
        dto.setAprobados(stats.getAprobados());
        dto.setReprobados(stats.getReprobados());
        dto.setPromedioNotas(stats.getPromedioNotas());
        dto.setPromedioDiasEstudio(stats.getPromedioDiasEstudio());
        dto.setPromedioHorasDiarias(stats.getPromedioHorasDiarias());
        dto.setPorcentajeAprobados(estadisticasHelper.calcularPorcentajeAprobados(stats));

        try {
            estadisticasHelper.cargarDistribuciones(dto, stats);
        } catch (JsonProcessingException ignored) {
        }

        if (stats.getFechaUltimaActualizacion() != null) {
            dto.setFechaUltimaActualizacion(stats.getFechaUltimaActualizacion().toLocalDate());
        }

        return dto;
    }
    private void calcularYGuardarEstadisticas(Materia materia) {
        EstadisticasMateria stats = calcularEstadisticas(materia);
        estadisticasRepo.save(stats);
    }
}