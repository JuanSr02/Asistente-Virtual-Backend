package com.recommendationSys.Sistema_Recomendador_Finales.services.rankingFinales;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.FinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.OrdenFinales;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Estudiante;
import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstudianteRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.HistoriaAcademicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingFinalesServiceImpl implements RankingFinalesService {

    private final EstudianteRepository estudianteRepo;
    private final HistoriaAcademicaRepository historiaAcademicaRepo;
    private final FinalesCalculator finalesCalculator;

    @Override
    public List<FinalDTO> obtenerFinalesParaRendir(Long estudianteId, OrdenFinales orden) {
        Estudiante estudiante = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        HistoriaAcademica historia = historiaAcademicaRepo.findByEstudiante(estudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Historia académica no encontrada"));

        return finalesCalculator.calcularFinalesParaRendir(historia, orden);
    }
}