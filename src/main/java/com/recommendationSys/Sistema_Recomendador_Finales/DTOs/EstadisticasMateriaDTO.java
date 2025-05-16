package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.Data;

import java.util.Map;

@Data
public class EstadisticasMateriaDTO {
    private String codigoMateria;
    private String nombreMateria;
    private int totalRendidos;
    private int aprobados;
    private int reprobados;
    private double porcentajeAprobados;
    private double promedioNotas;
    private double promedioDiasEstudio;
    private double promedioHorasDiarias;
    private Map<Integer, Integer> distribucionDificultad;
    private Map<String, Integer> distribucionModalidad;
    private Map<String, Integer> distribucionRecursos;
    private String ultimaActualizacion;
}