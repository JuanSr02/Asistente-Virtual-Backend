package com.asistenteVirtual.modules.historiaAcademica.dto;

import java.time.LocalDate;

public record DatosFila(
        String nombreMateria,
        String codigo,
        LocalDate fecha,
        String tipo,
        Double nota,
        String resultado
) {}