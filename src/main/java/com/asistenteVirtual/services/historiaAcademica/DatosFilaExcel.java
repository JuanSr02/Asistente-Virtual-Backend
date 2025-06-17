package com.asistenteVirtual.services.historiaAcademica;

import java.time.LocalDate;

public record DatosFilaExcel(
        String nombreMateria,
        String codigo,
        LocalDate fecha,
        String tipo,
        Double nota,
        String resultado
) {
}
