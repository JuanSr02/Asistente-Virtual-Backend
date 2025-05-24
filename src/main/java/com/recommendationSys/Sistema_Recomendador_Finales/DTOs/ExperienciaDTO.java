package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ExperienciaDTO {

    @NotNull(message = "El ID del examen es obligatorio")
    private Long examenId;

    @NotNull(message = "La dificultad es obligatoria")
    @Min(value = 1, message = "La dificultad debe ser al menos 1")
    @Max(value = 10, message = "La dificultad no puede ser mayor que 10")
    private Integer dificultad;

    @NotNull(message = "Los días de estudio son obligatorios")
    @Min(value = 0, message = "Los días de estudio no pueden ser negativos")
    private Integer diasEstudio;

    @NotNull(message = "Las horas diarias son obligatorias")
    @Min(value = 0, message = "Las horas diarias no pueden ser negativas")
    @Max(value = 24, message = "Las horas diarias no pueden exceder 24")
    private Integer horasDiarias;

    @NotNull(message = "Los intentos previos son obligatorios")
    @Min(value = 0, message = "Los intentos previos no pueden ser negativos")
    private Integer intentosPrevios;

    @Size(max = 20, message = "La modalidad no puede exceder los 20 caracteres")
    private String modalidad;

    @Size(max = 200, message = "Los recursos no pueden exceder los 200 caracteres")
    private String recursos;

    @Size(max = 100, message = "La motivación no puede exceder los 100 caracteres")
    private String motivacion;

}
