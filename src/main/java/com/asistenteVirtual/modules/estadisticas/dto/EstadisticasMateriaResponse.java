package com.asistenteVirtual.modules.estadisticas.dto;

import java.time.LocalDate;
import java.util.Map;

public record EstadisticasMateriaResponse(
    String codigoMateria,
    String nombreMateria,
    Integer totalRendidos,
    Integer aprobados,
    Integer reprobados,
    Double porcentajeAprobados,
    Double promedioNotas,
    Double promedioDiasEstudio,
    Double promedioHorasDiarias,
    Double promedioDificultad,
    Map<Integer, Integer> distribucionDificultad,
    Map<String, Integer> distribucionModalidad,
    Map<String, Integer> distribucionRecursos,
    LocalDate fechaUltimaActualizacion
) {}