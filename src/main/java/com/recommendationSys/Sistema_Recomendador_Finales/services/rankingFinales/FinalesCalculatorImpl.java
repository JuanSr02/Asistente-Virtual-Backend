package com.recommendationSys.Sistema_Recomendador_Finales.services.rankingFinales;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.FinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.OrdenFinales;
import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.CorrelativaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.EstadisticasMateriaRepository;
import com.recommendationSys.Sistema_Recomendador_Finales.repository.RenglonRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FinalesCalculatorImpl implements FinalesCalculator {

    private final RenglonRepository renglonRepo;
    private final CorrelativaRepository correlativaRepo;
    private final EstadisticasMateriaRepository estadisticasRepo;
    private final FinalesMapper finalesMapper;
    private final FinalesSorter finalesSorter;

    @Override
    public List<FinalDTO> calcularFinalesParaRendir(HistoriaAcademica historia, OrdenFinales orden) {
        List<Renglon> regularesAprobadas = obtenerRegularesAprobadas(historia);
        List<FinalDTO> finales = mapearARenglonDTO(regularesAprobadas);
        finalesSorter.ordenarFinales(finales, orden);
        return finales;
    }

    private List<Renglon> obtenerRegularesAprobadas(HistoriaAcademica historia) {
        return renglonRepo.findByHistoriaAcademicaAndTipoAndResultado(
                historia, "Regularidad", "Aprobado");
    }

    private List<FinalDTO> mapearARenglonDTO(List<Renglon> regularesAprobadas) {
        return regularesAprobadas.stream()
                .map(finalesMapper::toFinalDTO)
                .collect(Collectors.toList());
    }
}