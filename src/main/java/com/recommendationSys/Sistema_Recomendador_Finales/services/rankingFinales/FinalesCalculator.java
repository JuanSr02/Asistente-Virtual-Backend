package com.recommendationSys.Sistema_Recomendador_Finales.services.rankingFinales;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.FinalDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.OrdenFinales;
import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;

import java.util.List;

public interface FinalesCalculator {
    List<FinalDTO> calcularFinalesParaRendir(HistoriaAcademica historia, OrdenFinales orden);
}