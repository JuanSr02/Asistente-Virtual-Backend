package com.recommendationSys.Sistema_Recomendador_Finales.services.estudiante;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstudianteDto;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstudianteResponseDTO;

import java.util.List;

public interface EstudianteService {
    EstudianteResponseDTO crearEstudiante(EstudianteDto dto);
    EstudianteResponseDTO obtenerPorId(Long id);
    List<EstudianteResponseDTO> obtenerTodos();
    EstudianteResponseDTO actualizarEstudiante(Long id, EstudianteDto dto);
    void eliminarEstudiante(Long id);
}

