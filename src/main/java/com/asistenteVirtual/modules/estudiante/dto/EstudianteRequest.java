package com.asistenteVirtual.modules.estudiante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EstudianteRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50)
        String nombreApellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "Formato de email inválido")
        @Size(max = 50)
        String mail,

        @Size(max = 15)
        String telefono,

        @NotBlank(message = "ID de Supabase obligatorio")
        String supabaseUserId,

        @NotBlank(message = "El rol es obligatorio")
        String role
) {
}