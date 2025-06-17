package com.asistenteVirtual.services.inscripciones;

import com.asistenteVirtual.DTOs.InscripcionResponseDTO;
import com.asistenteVirtual.DTOs.RegistroInscripcionDTO;

import java.util.List;

public interface InscripcionService {
    InscripcionResponseDTO crearInscripcion(RegistroInscripcionDTO dto);
    void eliminarInscripcion(Long id);
    List<InscripcionResponseDTO> obtenerInscriptos(String materiaCodigo, Integer anio, String turno);
}

