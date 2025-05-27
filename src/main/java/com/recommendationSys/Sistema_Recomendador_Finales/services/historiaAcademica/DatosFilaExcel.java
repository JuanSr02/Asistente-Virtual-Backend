package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import java.time.LocalDate;

public record DatosFilaExcel(
        String nombreMateria,
        LocalDate fecha,
        String tipo,
        Double nota,
        String resultado
) {}
