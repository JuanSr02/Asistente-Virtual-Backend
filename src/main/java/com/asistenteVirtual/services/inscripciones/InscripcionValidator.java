package com.asistenteVirtual.services.inscripciones;

import com.asistenteVirtual.DTOs.RegistroInscripcionDTO;
import com.asistenteVirtual.exceptions.IntegrityException;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.model.Materia;
import com.asistenteVirtual.model.PlanDeEstudio;
import com.asistenteVirtual.repository.EstudianteRepository;
import com.asistenteVirtual.repository.MateriaRepository;
import com.asistenteVirtual.repository.PlanDeEstudioRepository;
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
        Materia materia = materiaRepository.findByCodigoAndPlanDeEstudio(dto.getMateriaCodigo(),plan).orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
        Estudiante estudiante = estudianteRepository.findById(dto.getEstudianteId()).orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));
        if (inscripcionRepo.existsByMateriaAndEstudianteAndAnioAndTurno(materia,estudiante,dto.getAnio(),dto.getTurno())){
            throw new IntegrityException(
                    String.format("El estudiante ya está inscripto en esta materia para el año %d y el turno %s", dto.getAnio(),dto.getTurno()));
        }
    }
}