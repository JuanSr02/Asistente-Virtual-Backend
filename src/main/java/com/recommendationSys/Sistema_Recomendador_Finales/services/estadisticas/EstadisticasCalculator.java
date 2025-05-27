package com.recommendationSys.Sistema_Recomendador_Finales.services.estadisticas;

import com.recommendationSys.Sistema_Recomendador_Finales.model.EstadisticasMateria;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;

public interface EstadisticasCalculator {
    EstadisticasMateria calcularEstadisticas(Materia materia);
}