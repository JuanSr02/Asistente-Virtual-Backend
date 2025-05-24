package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstadisticasFinalDTO {
    private double porcentajeAprobados;
    private double promedioNotas;
    private double promedioDiasEstudio;
    private double promedioHorasDiarias;
    private double promedioDificultad;
}
