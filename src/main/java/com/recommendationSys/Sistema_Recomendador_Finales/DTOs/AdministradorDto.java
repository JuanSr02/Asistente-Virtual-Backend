package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorDto {
    private String nombreApellido;
    private String mail;
    private String telefono;
    private String supabaseUserId;
}
