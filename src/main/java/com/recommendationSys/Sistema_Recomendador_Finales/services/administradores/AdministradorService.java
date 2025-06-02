package com.recommendationSys.Sistema_Recomendador_Finales.services.administradores;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.AdministradorDto;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.AdministradorResponseDTO;

import java.util.List;

public interface AdministradorService {
    AdministradorResponseDTO crearAdministrador(AdministradorDto dto);
    AdministradorResponseDTO obtenerPorId(Long id);
    List<AdministradorResponseDTO> obtenerTodos();
    AdministradorResponseDTO actualizarAdministrador(Long id, AdministradorDto dto);
    void eliminarAdministrador(Long id);
}

