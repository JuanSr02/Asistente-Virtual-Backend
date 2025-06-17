package com.asistenteVirtual.services.estudiante;

import com.asistenteVirtual.DTOs.ActualizarEstudianteDTO;
import com.asistenteVirtual.DTOs.EstudianteDto;
import com.asistenteVirtual.DTOs.EstudianteResponseDTO;

import java.util.List;

public interface EstudianteService {
    EstudianteResponseDTO crearEstudiante(EstudianteDto dto);
    EstudianteResponseDTO obtenerPorId(Long id);
    List<EstudianteResponseDTO> obtenerTodos();
    EstudianteResponseDTO actualizarEstudiante(Long id, ActualizarEstudianteDTO dto);
    void eliminarEstudiante(Long id);
}

