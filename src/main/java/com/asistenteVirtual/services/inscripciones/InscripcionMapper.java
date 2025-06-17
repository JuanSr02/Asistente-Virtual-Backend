package com.asistenteVirtual.services.inscripciones;

import com.asistenteVirtual.DTOs.InscripcionResponseDTO;
import com.asistenteVirtual.DTOs.RegistroInscripcionDTO;
import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.model.RegistroInscripcion;
import org.springframework.stereotype.Component;

@Component
public class InscripcionMapper {

    public RegistroInscripcion toEntity(RegistroInscripcionDTO dto, Materia materia, Estudiante estudiante) {
        return RegistroInscripcion.builder()
                .turno(dto.getTurno())
                .anio(dto.getAnio())
                .materia(materia)
                .estudiante(estudiante)
                .build();
    }

    public InscripcionResponseDTO toResponseDTO(RegistroInscripcion inscripcion) {
        return InscripcionResponseDTO.builder()
                .id(inscripcion.getId())
                .turno(inscripcion.getTurno())
                .anio(inscripcion.getAnio())
                .materiaCodigo(inscripcion.getMateria().getCodigo())
                .materiaNombre(inscripcion.getMateria().getNombre())
                .materiaPlan(inscripcion.getMateria().getPlanDeEstudio().getCodigo())
                .estudianteId(inscripcion.getEstudiante().getId())
                .estudianteNombre(inscripcion.getEstudiante().getNombreApellido())
                .build();
    }
}