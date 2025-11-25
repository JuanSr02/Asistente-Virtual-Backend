package com.asistenteVirtual.modules.estadisticas.dto;

import java.util.List;
import java.util.Map;

public record EstadisticasGeneralesResponse(
    Long estudiantesActivos,
    Integer totalMaterias,
    Integer totalExamenesRendidos,
    Double porcentajeAprobadosGeneral,
    Double promedioGeneral,
    Map<String, Integer> distribucionEstudiantesPorCarrera,
    Map<String, Integer> distribucionExamenesPorMateria,
    MateriaRankingResponse materiaMasRendida,
    Long cantidadMateriaMasRendida,
    List<MateriaRankingResponse> top5Aprobadas,
    List<MateriaRankingResponse> top5Reprobadas,
    Map<String, Double> promedioNotasPorMateria
) {}