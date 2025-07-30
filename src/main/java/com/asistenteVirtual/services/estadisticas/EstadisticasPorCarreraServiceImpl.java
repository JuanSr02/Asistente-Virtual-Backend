package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.MateriaRankingDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.EstadisticasPorCarrera;
import com.asistenteVirtual.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EstadisticasPorCarreraServiceImpl implements EstadisticasPorCarreraService {
    private final PlanDeEstudioRepository planDeEstudioRepository;
    private final MateriaRepository materiaRepository;
    private final ExamenRepository examenRepository;
    private final HistoriaAcademicaRepository historiaRepo;
    private final EstadisticasHelper estadisticasHelper;
    private final ObjectMapper objectMapper;
    private final EstadisticasPorCarreraRepository estadisticasPorCarreraRepo;


    @Override
    public EstadisticasGeneralesDTO obtenerEstadisticasPorCarreraRapido(String codigoPlan, PeriodoEstadisticas periodo) {
        EstadisticasPorCarrera stats = estadisticasPorCarreraRepo
                .findFirstByCodigoPlanAndPeriodoOrderByFechaUltimaActualizacionDesc(
                        codigoPlan, periodo.toString())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No hay estadísticas almacenadas para esta carrera y periodo"));

        try {
            return EstadisticasGeneralesDTO.builder()
                    .estudiantesActivos((long) stats.getEstudiantesActivos())
                    .totalMaterias(stats.getTotalMaterias())
                    .totalExamenesRendidos(stats.getTotalExamenesRendidos())
                    .porcentajeAprobadosGeneral(stats.getPorcentajeAprobadosGeneral())
                    .promedioGeneral(stats.getPromedioGeneral())
                    .distribucionExamenesPorMateria(
                            objectMapper.readValue(stats.getDistribucionExamenesPorMateria(),
                                    new TypeReference<Map<String, Integer>>() {
                                    }))
                    .top5Aprobadas(
                            objectMapper.readValue(stats.getTop5Aprobadas(),
                                    new TypeReference<List<MateriaRankingDTO>>() {
                                    }))
                    .top5Reprobadas(
                            objectMapper.readValue(stats.getTop5Reprobadas(),
                                    new TypeReference<List<MateriaRankingDTO>>() {
                                    }))
                    .promedioNotasPorMateria(
                            objectMapper.readValue(stats.getPromedioNotasPorMateria(),
                                    new TypeReference<Map<String, Double>>() {
                                    }))
                    .materiaMasRendida(
                            objectMapper.readValue(stats.getMateriaMasRendida(), MateriaRankingDTO.class))
                    .cantidadMateriaMasRendida(stats.getCantidadMateriaMasRendida())
                    .build();
        } catch (JsonProcessingException e) {
            log.error("Error al deserializar estadísticas por carrera", e);
            throw new RuntimeException("Error al procesar estadísticas almacenadas", e);
        }
    }
}