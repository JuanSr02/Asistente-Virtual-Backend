package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;

public interface EstadisticasPorCarreraService {
    EstadisticasGeneralesDTO obtenerEstadisticasPorCarreraRapido(String codigoPlan, PeriodoEstadisticas periodo);
}