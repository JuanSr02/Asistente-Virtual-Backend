package com.asistenteVirtual.services.estadisticas;

import com.asistenteVirtual.DTOs.EstadisticasGeneralesDTO;

public interface EstadisticasGeneralCalculator {
    EstadisticasGeneralesDTO obtenerEstadisticasGenerales();
    EstadisticasGeneralesDTO calcularEstadisticasGenerales();
}
