package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroInscripcionDTO {


    @NotBlank(message = "El turno es obligatorio")
    @Size(max = 50, message = "El turno no puede exceder los 50 caracteres")
    private String turno;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2012, message = "El año debe ser mayor o igual a 2012")
    @Max(value = 2028, message = "El año no puede ser menor o igual a 2028")
    private Integer anio;

    @NotBlank(message = "El código de materia es obligatorio")
    @Size(max = 15, message = "El código de materia no puede exceder los 15 caracteres")
    private String materiaCodigo;

    @NotNull(message = "El ID de estudiante es obligatorio")
    private Long estudianteId;

}
