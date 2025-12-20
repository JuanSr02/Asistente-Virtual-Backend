package com.asistenteVirtual.modules.historiaAcademica.dto;

import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;

public record HistoriaAcademicaCheck(
        Long id,
        Long persona_id_estudiante,
        String plan_de_estudio_codigo,
        String estado
) {
    public static HistoriaAcademicaCheck fromEntity(HistoriaAcademica h) {
        return new HistoriaAcademicaCheck(
                h.getId(),
                h.getEstudiante().getId(),
                h.getPlanDeEstudio().getCodigo(),
                h.getEstado()
        );
    }
}
