package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;
import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import com.asistenteVirtual.modules.historiaAcademica.model.Renglon;
import com.asistenteVirtual.modules.historiaAcademica.repository.RenglonRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinalesCalculatorImpl implements FinalesCalculator {

    private final RenglonRepository renglonRepo;
    private final FinalesMapper finalesMapper;
    private final FinalesSorter finalesSorter;

    @Override
    public List<FinalDTO> calcularFinalesParaRendir(HistoriaAcademica historia, OrdenFinales orden) {
        List<Renglon> regularesAprobadas = obtenerRegularesAprobadasHabilitadas(historia);
        List<FinalDTO> finales = mapearARenglonDTO(regularesAprobadas);
        finalesSorter.ordenarFinales(finales, orden);
        return finales;
    }

    public List<Renglon> obtenerRegularesAprobadasHabilitadas(HistoriaAcademica historia) {
        return renglonRepo.findRegularesHabilitadas(historia.getId());
    }

    public List<FinalDTO> mapearARenglonDTO(List<Renglon> regularesAprobadas) {
        return regularesAprobadas.stream()
                .map(finalesMapper::toFinalDTO)
                .collect(Collectors.toList());
    }
}