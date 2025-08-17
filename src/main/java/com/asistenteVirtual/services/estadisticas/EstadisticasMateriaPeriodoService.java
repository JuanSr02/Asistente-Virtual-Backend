package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasMateriaDTO;

public interface EstadisticasMateriaPeriodoService {
    EstadisticasMateriaDTO obtenerEstadisticasMateriaPorPeriodo(String codigoMateria, PeriodoEstadisticas periodo);
}
