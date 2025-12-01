package com.asistenteVirtual.modules.admin.dto;

import com.asistenteVirtual.modules.admin.model.Administrador;

public record AdministradorResponse(
        Long id,
        String nombreApellido,
        String mail,
        String telefono,
        String supabaseUserId
) {
    public static AdministradorResponse fromEntity(Administrador admin) {
        return new AdministradorResponse(
                admin.getId(),
                admin.getNombreApellido(),
                admin.getMail(),
                admin.getTelefono(),
                admin.getSupabaseUserId()
        );
    }
}