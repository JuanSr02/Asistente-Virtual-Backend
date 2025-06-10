package com.recommendationSys.Sistema_Recomendador_Finales.services.estadisticas;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasGeneralesDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.EstadisticasMateriaDTO;

public interface FastStatisticsService {
    EstadisticasGeneralesDTO getCachedGeneralStatistics();
    EstadisticasMateriaDTO getCachedMateriaStatistics(String codigoMateria);
}