package com.asistenteVirtual.services.inscripciones;

import com.asistenteVirtual.DTOs.RegistroInscripcionDTO;
import com.asistenteVirtual.common.exceptions.IntegrityException;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import com.asistenteVirtual.modules.planEstudio.repository.MateriaRepository;
import com.asistenteVirtual.modules.planEstudio.repository.PlanDeEstudioRepository;
import com.asistenteVirtual.repository.RegistroInscripcionRepository;
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
        Materia materia = materiaRepository.findByCodigoAndPlanDeEstudio(dto.getMateriaCodigo(), plan).orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        Estudiante estudiante = estudianteRepository.findById(dto.getEstudianteId()).orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        if (inscripcionRepo.existsByMateriaAndEstudianteAndAnioAndTurno(materia, estudiante, dto.getAnio(), dto.getTurno())) {
            throw new IntegrityException(
                    String.format("El estudiante ya está inscripto en esta materia para el año %d y el turno %s", dto.getAnio(), dto.getTurno()));
        }
    }
}