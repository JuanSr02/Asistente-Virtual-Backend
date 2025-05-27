package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class EstadisticasGeneralesDTO {
    private Integer totalMaterias;
    private Integer totalExamenesRendidos;
    private Double porcentajeAprobadosGeneral;
    private List<MateriaRankingDTO> top5Aprobadas;
    private List<MateriaRankingDTO> top5Reprobadas;
    private Map<String, Double> promedioNotasPorMateria;
}
