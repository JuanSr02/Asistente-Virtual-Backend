package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteDto {
    private String nombreApellido;
    private String mail;
    private String telefono;
    private String supabaseUserId;
    private Integer nroRegistro;
}

