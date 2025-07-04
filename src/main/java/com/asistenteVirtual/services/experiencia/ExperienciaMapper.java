package com.asistenteVirtual.services.experiencia;

import com.asistenteVirtual.DTOs.ActualizarExperienciaDTO;
import com.asistenteVirtual.DTOs.ExperienciaDTO;
import com.asistenteVirtual.DTOs.ExperienciaResponseDTO;
import com.asistenteVirtual.model.Examen;
import com.asistenteVirtual.model.Experiencia;
import com.asistenteVirtual.model.Materia;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ExperienciaMapper {

    public Experiencia toEntity(ExperienciaDTO dto, Examen examen) {
        return Experiencia.builder()
                .examen(examen)
                .dificultad(dto.getDificultad())
                .diasEstudio(dto.getDiasEstudio())
                .horasDiarias(dto.getHorasDiarias())
                .intentosPrevios(dto.getIntentosPrevios())
                .modalidad(dto.getModalidad())
                .recursos(dto.getRecursos())
                .motivacion(dto.getMotivacion())
                .linkResumen(dto.getLinkResumen())
                .build();
    }

    public void updateFromDto(ActualizarExperienciaDTO dto, Experiencia experiencia) {
        Optional.ofNullable(dto.getDificultad()).ifPresent(experiencia::setDificultad);
        Optional.ofNullable(dto.getDiasEstudio()).ifPresent(experiencia::setDiasEstudio);
        Optional.ofNullable(dto.getHorasDiarias()).ifPresent(experiencia::setHorasDiarias);
        Optional.ofNullable(dto.getIntentosPrevios()).ifPresent(experiencia::setIntentosPrevios);
        Optional.ofNullable(dto.getModalidad()).ifPresent(experiencia::setModalidad);
        Optional.ofNullable(dto.getRecursos()).ifPresent(experiencia::setRecursos);
        Optional.ofNullable(dto.getMotivacion()).ifPresent(experiencia::setMotivacion);
        Optional.ofNullable(dto.getLinkResumen()).ifPresent(experiencia::setLinkResumen);
    }

    public ExperienciaResponseDTO mapToExperienciaResponseDTO(Experiencia experiencia) {
        if (experiencia == null || experiencia.getExamen() == null || experiencia.getExamen().getRenglon() == null)
            return null;

        Materia materia = experiencia.getExamen().getRenglon().getMateria();
        return ExperienciaResponseDTO.builder()
                .id(experiencia.getId())
                .dificultad(experiencia.getDificultad())
                .diasEstudio(experiencia.getDiasEstudio())
                .horasDiarias(experiencia.getHorasDiarias())
                .intentosPrevios(experiencia.getIntentosPrevios())
                .modalidad(experiencia.getModalidad())
                .recursos(experiencia.getRecursos())
                .motivacion(experiencia.getMotivacion())
                .fechaExamen(experiencia.getExamen().getFecha())
                .nota(experiencia.getExamen().getNota())
                .codigoMateria(materia.getCodigo())
                .nombreMateria(materia.getNombre())
                .linkResumen(experiencia.getLinkResumen())
                .build();
    }
}
