package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanEstudioResponseDTO {
    private String codigo;
    private String propuesta;
    private Long cantidadMateriasCargadas;
}
