package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;

public interface EstadisticasAvanzadasService {
    EstadisticasGeneralesDTO obtenerEstadisticasPorCarrera(String codigoPlan, PeriodoEstadisticas periodo);
}
