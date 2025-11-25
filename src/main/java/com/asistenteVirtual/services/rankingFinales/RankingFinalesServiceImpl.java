package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import com.asistenteVirtual.modules.planEstudio.dto.MateriaResponse;
import com.asistenteVirtual.repository.HistoriaAcademicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    @Override
    public List<MateriaResponse> obtenerFinalesParaInscribirse(Long estudianteId) {
        Estudiante estudiante = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        HistoriaAcademica historia = historiaAcademicaRepo.findByEstudiante(estudiante)
                .orElseThrow(() -> new ResourceNotFoundException("Historia académica no encontrada"));
        List<FinalDTO> finales = finalesCalculator.mapearARenglonDTO(finalesCalculator.obtenerRegularesAprobadasHabilitadas(historia));
        List<MateriaResponse> finalesAInscribirse = new ArrayList<>();
        for (FinalDTO finalDTO : finales) {
            finalesAInscribirse.add(MateriaResponse.builder().codigo(finalDTO.getCodigoMateria()).nombre(finalDTO.getNombreMateria()).build());
        }
        return finalesAInscribirse;
    }
}