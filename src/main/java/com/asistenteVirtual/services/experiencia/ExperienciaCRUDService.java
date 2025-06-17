package com.asistenteVirtual.services.experiencia;

import com.asistenteVirtual.DTOs.ActualizarExperienciaDTO;
import com.asistenteVirtual.DTOs.ExperienciaDTO;
import com.asistenteVirtual.DTOs.ExperienciaResponseDTO;

import java.util.List;

public interface ExperienciaCRUDService {
    ExperienciaResponseDTO crearExperiencia(ExperienciaDTO experienciaDTO);
    ExperienciaResponseDTO obtenerExperienciaPorId(Long id);
    List<ExperienciaResponseDTO> obtenerTodasLasExperiencias();
    ExperienciaResponseDTO actualizarExperiencia(Long id, ActualizarExperienciaDTO dto);
    void eliminarExperiencia(Long id);
}
