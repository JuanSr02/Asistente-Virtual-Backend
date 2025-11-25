package com.asistenteVirtual.modules.experiencia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ExperienciaRequest(
        @NotNull(message = "El ID del examen es obligatorio")
        Long examenId,

        @NotNull @Min(1) @Max(10)
        Integer dificultad,

        @NotNull @Min(0)
        Integer diasEstudio,

        @NotNull @Min(0) @Max(24)
        Integer horasDiarias,

        @NotNull @Min(0)
        Integer intentosPrevios,

        @Size(max = 20)
        String modalidad,

        @Size(max = 200)
        String recursos,

        @Size(max = 100)
        String motivacion,

        String linkResumen
) {
}