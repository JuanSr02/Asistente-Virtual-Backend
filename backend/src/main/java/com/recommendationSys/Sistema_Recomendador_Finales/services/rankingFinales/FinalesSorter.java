package com.recommendationSys.Sistema_Recomendador_Finales.services.rankingFinales;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.FinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.OrdenFinales;

import java.util.List;

public interface FinalesSorter {
    void ordenarFinales(List<FinalDTO> finales, OrdenFinales orden);
}