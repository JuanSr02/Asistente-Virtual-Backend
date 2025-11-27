package com.asistenteVirtual.modules.historiaAcademica.dto;

import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
import java.time.LocalDate;

public record HistoriaAcademicaResponse(
    String nombreCompleto,
    String codigoPlan,
    String estado,
    LocalDate fechaUltimaActualizacion,
    int cantidadMateriasRegistradas
) {
    public static HistoriaAcademicaResponse fromEntity(HistoriaAcademica h) {
        return new HistoriaAcademicaResponse(
            h.getEstudiante().getNombreApellido(),
            h.getPlanDeEstudio().getCodigo(),
            h.getEstado(),
            LocalDate.now(),
            h.getRenglones().size()
        );
    }
}