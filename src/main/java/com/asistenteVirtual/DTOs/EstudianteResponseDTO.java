package com.asistenteVirtual.DTOs;

import com.asistenteVirtual.model.Estudiante;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la respuesta del recurso Estudiante.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteResponseDTO {
    private Long id;
    private String nombreApellido;
    private String mail;
    private String telefono;
    private String supabaseUserId;

    /**
     * Convierte una entidad Estudiante a su correspondiente DTO de respuesta.
     *
     * @param estudiante entidad Estudiante
     * @return DTO con los datos del estudiante
     */
    public static EstudianteResponseDTO fromEntity(Estudiante estudiante) {
        return EstudianteResponseDTO.builder()
                .id(estudiante.getId())
                .nombreApellido(estudiante.getNombreApellido())
                .mail(estudiante.getMail())
                .telefono(estudiante.getTelefono())
                .supabaseUserId(estudiante.getSupabaseUserId())
                .build();
    }
}

