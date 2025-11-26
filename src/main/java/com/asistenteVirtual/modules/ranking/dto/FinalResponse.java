package com.asistenteVirtual.modules.ranking.dto;

import java.time.LocalDate;

public record FinalResponse(
        String codigoMateria,
        String nombreMateria,
        LocalDate fechaRegularidad,
        LocalDate fechaVencimiento,
        long semanasParaVencimiento,
        long vecesEsCorrelativa,
        EstadisticasFinalResponse estadisticas
) {
}