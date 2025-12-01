package com.asistenteVirtual.modules.planEstudio.dto;

public record PlanEstudioResponse(
        String codigo,
        String propuesta,
        Long cantidadMaterias
) {
}