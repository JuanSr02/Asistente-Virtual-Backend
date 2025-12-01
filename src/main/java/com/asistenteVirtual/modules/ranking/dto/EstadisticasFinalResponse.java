package com.asistenteVirtual.modules.ranking.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class EstadisticasFinalResponse {
    private Double porcentajeAprobados;
    private Double promedioNotas;
    private Double promedioDiasEstudio;
    private Double promedioHorasDiarias;
    private Double promedioDificultad;
}
