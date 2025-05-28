package com.recommendationSys.Sistema_Recomendador_Finales.services.inscripciones;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.InscripcionResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.RegistroInscripcionDTO;

import java.util.List;

public interface InscripcionService {
    InscripcionResponseDTO crearInscripcion(RegistroInscripcionDTO dto);
    void eliminarInscripcion(Long id);
    List<InscripcionResponseDTO> obtenerInscriptos(String materiaCodigo, Integer anio, String turno,String codigoPlan);
}

