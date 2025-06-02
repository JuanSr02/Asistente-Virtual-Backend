package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorDto {

    @Size(max = 50, message = "El nombre y apellido no puede superar los 50 caracteres")
    @NotBlank(message = "El nombre y apellido es obligatorio")
    private String nombreApellido;

    @Email
    @Size(max = 50, message = "El mail no puede superar los 50 caracteres")
    @NotBlank(message = "El mail es obligatorio")
    private String mail;

    @Size(max = 15, message = "El telefono no puede superar los 15 caracteres")
    private String telefono;

    @NotBlank(message = "SupabaseUserId es obligatorio")
    private String supabaseUserId;
}
