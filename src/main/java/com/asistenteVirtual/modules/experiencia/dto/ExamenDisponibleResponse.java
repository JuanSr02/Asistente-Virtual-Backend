package com.asistenteVirtual.modules.experiencia.dto;

import com.asistenteVirtual.modules.historiaAcademica.model.Examen;

import java.time.LocalDate;

public record ExamenDisponibleResponse(
        Long id,
        LocalDate fecha,
        Double nota,
        Long renglonId,
        String materiaCodigo,
        String materiaNombre
) {
    public static ExamenDisponibleResponse fromEntity(Examen e) {
        return new ExamenDisponibleResponse(
                e.getId(),
                e.getFecha(),
                e.getNota(),
                e.getRenglon().getId(),
                e.getRenglon().getMateria().getCodigo(),
                e.getRenglon().getMateria().getNombre()
        );
    }
}