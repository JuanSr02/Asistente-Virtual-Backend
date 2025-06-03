package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class HistoriaAcademicaResponseDTO {
    private String nombreCompleto;
    private String codigoPlan;
    private LocalDate fechaUltimaActualizacion;
    private Long renglonesCargados;
}
