package com.recommendationSys.Sistema_Recomendador_Finales.services.inscripciones;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.InscripcionResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.RegistroInscripcionDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.RegistroInscripcion;
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
                .estudianteId(inscripcion.getEstudiante().getId())
                .estudianteNombre(inscripcion.getEstudiante().getNombreApellido())
                .estudianteNroRegistro(inscripcion.getEstudiante().getNroRegistro())
                .build();
    }
}