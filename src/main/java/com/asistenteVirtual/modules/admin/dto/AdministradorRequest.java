package com.asistenteVirtual.modules.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdministradorRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50)
        String nombreApellido,

        @NotBlank(message = "El email es obligatorio")
        @Email
        @Size(max = 50)
        String mail,

        @Size(max = 15)
        String telefono,

        @NotBlank(message = "El ID de Supabase es obligatorio")
        String supabaseUserId,

        @NotBlank
        String role
) {
}