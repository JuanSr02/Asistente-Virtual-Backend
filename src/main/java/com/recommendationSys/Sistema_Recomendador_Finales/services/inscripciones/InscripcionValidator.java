package com.recommendationSys.Sistema_Recomendador_Finales.services.inscripciones;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.RegistroInscripcionDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.IntegrityException;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstudianteRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.RegistroInscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InscripcionValidator {
    private final RegistroInscripcionRepository inscripcionRepo;
    private final MateriaRepository materiaRepository;
    private final EstudianteRepository estudianteRepository;

    public void validarInscripcion(RegistroInscripcionDTO dto) {
        if (inscripcionRepo.existsByMateriaAndEstudianteAndAnio(
                materiaRepository.findById(dto.getMateriaCodigo()).orElseThrow(),
                estudianteRepository.findById(dto.getEstudianteId()).orElseThrow(),
                dto.getAnio())) {
            throw new IntegrityException(
                    String.format("El estudiante ya está inscripto en esta materia para el año %d", dto.getAnio()));
        }
    }
}