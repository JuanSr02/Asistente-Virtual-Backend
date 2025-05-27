package com.recommendationSys.Sistema_Recomendador_Finales.services.experiencia;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ActualizarExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.ExperienciaDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Examen;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Experiencia;
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
    }
}
