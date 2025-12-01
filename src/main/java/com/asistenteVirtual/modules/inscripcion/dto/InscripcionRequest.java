package com.asistenteVirtual.modules.inscripcion.dto;

import jakarta.validation.constraints.*;

public record InscripcionRequest(
        @NotBlank(message = "El turno es obligatorio")
        @Size(max = 50)
        String turno,

        @NotNull(message = "El año es obligatorio")
        @Min(2012) @Max(2028)
        Integer anio,

        @NotBlank(message = "El código de materia es obligatorio")
        @Size(max = 15)
        String materiaCodigo,

        @NotBlank(message = "El plan de la materia es obligatorio")
        @Size(max = 15)
        String materiaPlan,

        @NotNull(message = "El ID de estudiante es obligatorio")
        Long estudianteId
) {
}