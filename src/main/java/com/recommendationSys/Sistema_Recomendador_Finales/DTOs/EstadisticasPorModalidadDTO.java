package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.Data;

@Data
public class EstadisticasPorModalidadDTO {
    private String modalidad;
    private Long cantidad;
    private Double promedioNota;

    public EstadisticasPorModalidadDTO(String modalidad, Long cantidad, Double promedioNota) {
        this.modalidad = modalidad;
        this.cantidad = cantidad;
        this.promedioNota = promedioNota;
    }


}
