package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FinalDTO {

    private String codigoMateria;
    private String nombreMateria;

    private LocalDate fechaRegularidad;

    private LocalDate fechaVencimiento;
    private long semanasParaVencimiento;
    private long vecesEsCorrelativa;

    private EstadisticasFinalDTO estadisticas;
}
