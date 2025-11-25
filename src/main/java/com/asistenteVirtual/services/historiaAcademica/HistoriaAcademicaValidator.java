package com.asistenteVirtual.services.historiaAcademica;

import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.repository.HistoriaAcademicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HistoriaAcademicaValidator {

    private final EstudianteRepository estudianteRepo;
    private final HistoriaAcademicaRepository historiaAcademicaRepository;

    public void validarEstudiante(Long estudianteId) {
        if (!estudianteRepo.existsById(estudianteId)) {
            throw new ResourceNotFoundException("No se encontró el estudiante con ID " + estudianteId);
        }
    }

    public boolean validarHistoria(Long estudianteId) {
        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        return historiaAcademicaRepository.existsByEstudiante(estudiante);
    }
}