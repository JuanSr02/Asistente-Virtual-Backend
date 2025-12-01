package com.asistenteVirtual.modules.experiencia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

// Usamos un Record separado para actualizaciones (PATCH) donde los campos son opcionales
public record ExperienciaUpdate(
        @Min(1) @Max(10) Integer dificultad,
        @Min(0) Integer diasEstudio,
        @Min(0) @Max(24) Integer horasDiarias,
        @Min(0) Integer intentosPrevios,
        @Size(max = 20) String modalidad,
        @Size(max = 200) String recursos,
        @Size(max = 100) String motivacion,
        String linkResumen
) {
}