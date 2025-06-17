package com.asistenteVirtual.services.administradores;

import com.asistenteVirtual.DTOs.ActualizarAdministradorDTO;
import com.asistenteVirtual.DTOs.AdministradorDto;
import com.asistenteVirtual.DTOs.AdministradorResponseDTO;

import java.util.List;

public interface AdministradorService {
    AdministradorResponseDTO crearAdministrador(AdministradorDto dto);
    AdministradorResponseDTO obtenerPorId(Long id);
    List<AdministradorResponseDTO> obtenerTodos();
    AdministradorResponseDTO actualizarAdministrador(Long id, ActualizarAdministradorDTO dto);
    void eliminarAdministrador(Long id);
}

