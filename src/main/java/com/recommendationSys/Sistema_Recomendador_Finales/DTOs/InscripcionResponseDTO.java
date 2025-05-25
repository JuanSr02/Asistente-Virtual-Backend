package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.Data;

@Data
public class InscripcionResponseDTO {
    private Long id;
    private String turno;
    private Integer anio;
    private String materiaNombre;
    private String materiaCodigo;
    private String estudianteNombre;
    private Long estudianteId;
    private Integer estudianteNroRegistro;
}
