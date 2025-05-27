package com.recommendationSys.Sistema_Recomendador_Finales.services.rankingFinales;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.FinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.OrdenFinales;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class FinalesSorterImpl implements FinalesSorter {

    @Override
    public void ordenarFinales(List<FinalDTO> finales, OrdenFinales orden) {
        switch (orden) {
            case CORRELATIVAS:
                finales.sort(Comparator.comparingLong(FinalDTO::getVecesEsCorrelativa).reversed());
                break;

            case VENCIMIENTO:
                finales.sort(Comparator.comparingLong(FinalDTO::getSemanasParaVencimiento));
                break;

            case ESTADISTICAS:
                finales.sort(Comparator.comparingDouble(this::calcularPuntajeEstadisticas).reversed());
                break;
        }
    }

    private double calcularPuntajeEstadisticas(FinalDTO finalDTO) {
        if (finalDTO.getEstadisticas() == null) {
            return 0.0;
        }
        return finalDTO.getEstadisticas().getPorcentajeAprobados() /
                finalDTO.getEstadisticas().getPromedioDificultad();
    }
}