package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class EstadisticasGeneralesDTO {
    private int totalMaterias;
    private int totalExamenesRendidos;
    private double porcentajeAprobadosGeneral;
    private List<MateriaRankingDTO> top5Aprobadas;
    private List<MateriaRankingDTO> top5Reprobadas;
    private Map<String, Double> promedioNotasPorMateria;
}