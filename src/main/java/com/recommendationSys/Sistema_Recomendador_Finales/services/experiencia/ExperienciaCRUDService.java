package com.recommendationSys.Sistema_Recomendador_Finales.services.experiencia;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ActualizarExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaResponseDTO;

import java.util.List;

public interface ExperienciaCRUDService {
    ExperienciaResponseDTO crearExperiencia(ExperienciaDTO experienciaDTO);
    ExperienciaResponseDTO obtenerExperienciaPorId(Long id);
    List<ExperienciaResponseDTO> obtenerTodasLasExperiencias();
    ExperienciaResponseDTO actualizarExperiencia(Long id, ActualizarExperienciaDTO dto);
    void eliminarExperiencia(Long id);
}
