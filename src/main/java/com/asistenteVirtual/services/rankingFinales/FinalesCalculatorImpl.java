package com.asistenteVirtual.services.rankingFinales;

import com.asistenteVirtual.DTOs.FinalDTO;
import com.asistenteVirtual.DTOs.OrdenFinales;
import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.model.Renglon;
import com.asistenteVirtual.repository.CorrelativaRepository;
import com.asistenteVirtual.repository.EstadisticasMateriaRepository;
import com.asistenteVirtual.repository.RenglonRepository;
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