package com.asistenteVirtual.modules.planEstudio.dto;

import lombok.Builder;

@Builder
public record MateriaResponse(
        String codigo,
        String nombre
) {
}