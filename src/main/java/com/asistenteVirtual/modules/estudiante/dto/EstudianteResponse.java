package com.asistenteVirtual.modules.estudiante.dto;

import com.asistenteVirtual.modules.estudiante.model.Estudiante;

public record EstudianteResponse(
        Long id,
        String nombreApellido,
        String mail,
        String telefono,
        String supabaseUserId
) {
    // Mapper estático conveniente
    public static EstudianteResponse fromEntity(Estudiante e) {
        return new EstudianteResponse(
                e.getId(),
                e.getNombreApellido(),
                e.getMail(),
                e.getTelefono(),
                e.getSupabaseUserId()
        );
    }
}