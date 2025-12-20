package com.asistenteVirtual.modules.estudiante.dto;

import com.asistenteVirtual.common.model.Persona;

public record PersonaResponse(
        Long id,
        String nombre_apellido,
        String mail,
        String telefono,
        String supabase_user_id,
        String rol_usuario
) {
    // Mapper estático conveniente
    public static PersonaResponse fromEntity(Persona e) {
        return new PersonaResponse(
                e.getId(),
                e.getNombreApellido(),
                e.getMail(),
                e.getTelefono(),
                e.getSupabaseUserId(),
                e.getRolUsuario()
        );
    }
}