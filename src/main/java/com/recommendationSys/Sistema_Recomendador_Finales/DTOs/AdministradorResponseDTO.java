package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Administrador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa la respuesta del recurso Administrador.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdministradorResponseDTO {
    private Long id;
    private String nombreApellido;
    private String mail;
    private String telefono;
    private String supabaseUserId;

    /**
     * Convierte una entidad Administrador a su correspondiente DTO de respuesta.
     *
     * @param administrador entidad Administrador
     * @return DTO con los datos del administrador
     */
    public static AdministradorResponseDTO fromEntity(Administrador administrador) {
        return AdministradorResponseDTO.builder()
                .id(administrador.getId())
                .nombreApellido(administrador.getNombreApellido())
                .mail(administrador.getMail())
                .telefono(administrador.getTelefono())
                .supabaseUserId(administrador.getSupabaseUserId())
                .build();
    }
}

