package com.recommendationSys.Sistema_Recomendador_Finales.services.estadisticas;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasMateriaDTO;

public interface EstadisticasCalculator {
    public EstadisticasMateriaDTO obtenerEstadisticasMateria(String materia,String plan);
    public EstadisticasMateriaDTO obtenerEstadisticasSuperMateria(String materia);

}