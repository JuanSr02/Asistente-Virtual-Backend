package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;
import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;

public interface FastStatisticsService {
    EstadisticasGeneralesDTO getCachedGeneralStatistics();
    EstadisticasMateriaDTO getCachedMateriaStatistics(String codigoMateria);
}