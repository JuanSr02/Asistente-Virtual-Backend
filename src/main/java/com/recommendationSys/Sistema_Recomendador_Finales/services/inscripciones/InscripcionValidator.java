package com.recommendationSys.Sistema_Recomendador_Finales.services.inscripciones;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.RegistroInscripcionDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.IntegrityException;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstudianteRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.MateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.PlanDeEstudioRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.RegistroInscripcionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InscripcionValidator {
    private final RegistroInscripcionRepository inscripcionRepo;
    private final MateriaRepository materiaRepository;
    private final EstudianteRepository estudianteRepository;
    private final PlanDeEstudioRepository planDeEstudioRepository;

    public void validarInscripcion(RegistroInscripcionDTO dto) {
        PlanDeEstudio plan = planDeEstudioRepository.findById(dto.getMateriaPlan())
                .orElseThrow(() -> new ResourceNotFoundException("Plan de estudios no encontrado"));
        Materia materia = materiaRepository.findByCodigoAndPlanDeEstudio(dto.getMateriaCodigo(),plan).orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        Estudiante estudiante = estudianteRepository.findById(dto.getEstudianteId()).orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        if (inscripcionRepo.existsByMateriaAndEstudianteAndAnioAndTurno(materia,estudiante,dto.getAnio(),dto.getTurno())){
            throw new IntegrityException(
                    String.format("El estudiante ya está inscripto en esta materia para el año %d y el turno %s", dto.getAnio(),dto.getTurno()));
        }
    }
}