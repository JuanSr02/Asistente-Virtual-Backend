package com.asistenteVirtual.services.historiaAcademica;

import com.asistenteVirtual.exceptions.IntegrityException;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.repository.EstudianteRepository;
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

    public void validarHistoria(Long estudianteId){
        Estudiante estudiante = estudianteRepo.findById(estudianteId).orElseThrow();
        if(historiaAcademicaRepository.existsByEstudiante(estudiante)){
            throw new IntegrityException("El estudiante con ID " + estudianteId +
                    " ya tiene una historia cargada");
        }
    }
}