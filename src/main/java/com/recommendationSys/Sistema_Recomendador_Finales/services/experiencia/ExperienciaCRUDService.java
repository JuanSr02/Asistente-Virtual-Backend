package com.recommendationSys.Sistema_Recomendador_Finales.services.experiencia;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ActualizarExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;

import java.util.List;

public interface ExperienciaCRUDService {
    Experiencia crearExperiencia(ExperienciaDTO experienciaDTO);
    Experiencia obtenerExperienciaPorId(Long id);
    List<Experiencia> obtenerTodasLasExperiencias();
    Experiencia actualizarExperiencia(Long id, ActualizarExperienciaDTO dto);
    void eliminarExperiencia(Long id);
}
