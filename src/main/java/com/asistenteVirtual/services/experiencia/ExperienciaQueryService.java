package com.asistenteVirtual.services.experiencia;

import com.asistenteVirtual.DTOs.ExamenDTO;
import com.asistenteVirtual.DTOs.ExperienciaResponseDTO;

import java.util.List;

public interface ExperienciaQueryService {
    List<ExperienciaResponseDTO> obtenerExperienciasPorMateria(String codigoMateria);

    List<ExperienciaResponseDTO> obtenerExperienciasPorEstudiante(Long idEstudiante);

    List<ExamenDTO> obtenerExamenesPorEstudiante(Long idEstudiante);

}
