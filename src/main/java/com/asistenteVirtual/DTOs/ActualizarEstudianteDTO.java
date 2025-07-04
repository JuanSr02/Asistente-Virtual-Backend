package com.asistenteVirtual.DTOs;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualizar parcialmente los datos de un Estudiante.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstudianteDTO {

    @Size(max = 50, message = "El nombre y apellido no puede superar los 50 caracteres")
    private String nombreApellido;

    @Email
    @Size(max = 50, message = "El mail no puede superar los 50 caracteres")
    private String mail;

    @Size(max = 15, message = "El telefono no puede superar los 15 caracteres")
    private String telefono;

    private String contrasenia;

}
