package com.asistenteVirtual.services.experiencia;

import com.asistenteVirtual.DTOs.ActualizarExperienciaDTO;
import com.asistenteVirtual.DTOs.ExperienciaDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.Examen;
import com.asistenteVirtual.repository.ExamenRepository;
import com.asistenteVirtual.repository.ExperienciaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ExperienciaValidator {
    private final ExperienciaRepository experienciaRepository;
    private final ExamenRepository examenRepository;

    public void validarCreacionExperiencia(ExperienciaDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El DTO de experiencia no puede ser nulo");
        }

        Examen examen = examenRepository.findById(dto.getExamenId())
                .orElseThrow(() -> new ResourceNotFoundException("Examen no encontrado"));

        if (experienciaRepository.existsByExamen(examen)) {
            throw new IllegalStateException("Ya existe una experiencia para este examen");
        }
    }

    public void validarActualizacionExperiencia(ActualizarExperienciaDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El DTO de actualización no puede ser nulo");
        }

        if (Stream.of(
                dto.getDificultad(),
                dto.getDiasEstudio(),
                dto.getHorasDiarias(),
                dto.getIntentosPrevios(),
                dto.getModalidad(),
                dto.getRecursos(),
                dto.getMotivacion()
        ).allMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Debe proporcionar al menos un campo para actualizar");
        }
    }
}
