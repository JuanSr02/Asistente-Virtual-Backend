package com.recommendationSys.Sistema_Recomendador_Finales.services.experiencia;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaResponseDTO;

import java.util.List;

public interface ExperienciaQueryService {
    List<ExperienciaResponseDTO> obtenerExperienciasPorMateria(String codigoMateria);
    List<ExperienciaResponseDTO> obtenerExperienciasPorEstudiante(Long idEstudiante);

}
