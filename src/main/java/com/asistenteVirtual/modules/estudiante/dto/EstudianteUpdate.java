package com.asistenteVirtual.modules.estudiante.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record EstudianteUpdate(
        @Size(max = 50) String nombreApellido,
        @Email @Size(max = 50) String mail,
        @Size(max = 15) String telefono,
        String contrasenia // Solo se usa para enviarlo a Supabase si aplica
) {
}