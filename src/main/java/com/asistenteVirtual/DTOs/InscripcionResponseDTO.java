package com.asistenteVirtual.DTOs;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InscripcionResponseDTO {
    private Long id;
    private String turno;
    private Integer anio;
    private String materiaNombre;
    private String materiaCodigo;
    private String materiaPlan;
    private String estudianteNombre;
    private Long estudianteId;
}
