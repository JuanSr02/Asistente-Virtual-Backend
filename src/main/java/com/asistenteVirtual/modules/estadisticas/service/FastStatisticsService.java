package com.asistenteVirtual.modules.estadisticas.service;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.common.utils.JsonConverter;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasGeneralesResponse;
import com.asistenteVirtual.modules.estadisticas.dto.EstadisticasMateriaResponse;
import com.asistenteVirtual.modules.estadisticas.dto.MateriaRankingResponse;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasGenerales;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasMateria;
import com.asistenteVirtual.modules.estadisticas.model.EstadisticasPorCarrera;
import com.asistenteVirtual.modules.estadisticas.model.PeriodoEstadisticas;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasGeneralesRepository;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasMateriaRepository;
import com.asistenteVirtual.modules.estadisticas.repository.EstadisticasPorCarreraRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FastStatisticsService {

    private final EstadisticasGeneralesRepository generalesRepo;
    private final EstadisticasMateriaRepository materiaRepo;
    private final EstadisticasPorCarreraRepository carreraRepo;
    private final JsonConverter jsonConverter;

    @Transactional(readOnly = true)
    public EstadisticasGeneralesResponse getCachedGeneralStatistics() {
        EstadisticasGenerales stats = generalesRepo.findFirstByOrderByFechaUltimaActualizacionDesc()
                .orElseThrow(() -> new ResourceNotFoundException("No hay estadísticas generales calculadas aún."));
        return mapGeneralToResponse(stats);
    }

    @Transactional(readOnly = true)
    public EstadisticasMateriaResponse getCachedMateriaStatistics(String codigoMateria, PeriodoEstadisticas periodo) {
        EstadisticasMateria stats = materiaRepo.findByCodigoMateriaAndPeriodo(codigoMateria, periodo.toString())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay estadísticas cacheadas para materia: " + codigoMateria + " en periodo: " + periodo));

        return new EstadisticasMateriaResponse(
                stats.getCodigoMateria(),
                stats.getNombreMateria(),
                stats.getTotalRendidos(),
                stats.getAprobados(),
                stats.getReprobados(),
                calcularPorcentaje(stats.getAprobados(), stats.getTotalRendidos()),
                stats.getPromedioNotas(),
                stats.getPromedioDiasEstudio(),
                stats.getPromedioHorasDiarias(),
                stats.getPromedioDificultad(),
                jsonConverter.fromJson(stats.getDistribucionDificultad(), new TypeReference<Map<Integer, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getDistribucionModalidad(), new TypeReference<Map<String, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getDistribucionRecursos(), new TypeReference<Map<String, Integer>>() {
                }),
                stats.getFechaUltimaActualizacion().toLocalDate()
        );
    }

    @Transactional(readOnly = true)
    public EstadisticasGeneralesResponse getCachedCarreraStatistics(String codigoPlan, PeriodoEstadisticas periodo) {
        EstadisticasPorCarrera stats = carreraRepo.findByCodigoPlanAndPeriodo(codigoPlan, periodo.toString())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay estadísticas cacheadas para el plan " + codigoPlan + " y periodo " + periodo));

        return new EstadisticasGeneralesResponse(
                (long) stats.getEstudiantesActivos(),
                stats.getTotalMaterias(),
                stats.getTotalExamenesRendidos(),
                stats.getPorcentajeAprobadosGeneral(),
                stats.getPromedioGeneral(),
                Map.of(),
                jsonConverter.fromJson(stats.getDistribucionExamenesPorMateria(), new TypeReference<Map<String, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getMateriaMasRendida(), MateriaRankingResponse.class),
                stats.getCantidadMateriaMasRendida(),
                jsonConverter.fromJson(stats.getTop5Aprobadas(), new TypeReference<List<MateriaRankingResponse>>() {
                }),
                jsonConverter.fromJson(stats.getTop5Reprobadas(), new TypeReference<List<MateriaRankingResponse>>() {
                }),
                jsonConverter.fromJson(stats.getPromedioNotasPorMateria(), new TypeReference<Map<String, Double>>() {
                })
        );
    }

    private Double calcularPorcentaje(long parte, long total) {
        return total > 0 ? (double) parte / total * 100 : 0.0;
    }

    private EstadisticasGeneralesResponse mapGeneralToResponse(EstadisticasGenerales stats) {
        return new EstadisticasGeneralesResponse(
                stats.getEstudiantesActivos(),
                stats.getTotalMaterias(),
                stats.getTotalExamenesRendidos(),
                stats.getPorcentajeAprobadosGeneral(),
                stats.getPromedioGeneral(),
                jsonConverter.fromJson(stats.getDistribucionEstudiantesPorCarrera(), new TypeReference<Map<String, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getDistribucionExamenesPorMateria(), new TypeReference<Map<String, Integer>>() {
                }),
                jsonConverter.fromJson(stats.getMateriaMasRendida(), MateriaRankingResponse.class),
                stats.getCantidadMateriaMasRendida(),
                jsonConverter.fromJson(stats.getTop5Aprobadas(), new TypeReference<List<MateriaRankingResponse>>() {
                }),
                jsonConverter.fromJson(stats.getTop5Reprobadas(), new TypeReference<List<MateriaRankingResponse>>() {
                }),
                jsonConverter.fromJson(stats.getPromedioNotasPorMateria(), new TypeReference<Map<String, Double>>() {
                })
        );
    }
}