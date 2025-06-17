package com.asistenteVirtual.DTOs;

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
