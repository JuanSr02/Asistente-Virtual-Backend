package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.IntegrityException;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstudianteRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.HistoriaAcademicaRepository;
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