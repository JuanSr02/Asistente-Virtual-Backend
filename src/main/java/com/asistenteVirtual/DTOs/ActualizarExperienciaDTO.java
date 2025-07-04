package com.asistenteVirtual.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ActualizarExperienciaDTO {

    @Min(1)
    @Max(10)
    private Integer dificultad;

    @Min(0)
    private Integer diasEstudio;

    @Min(0)
    @Max(24)
    private Integer horasDiarias;

    @Min(0)
    private Integer intentosPrevios;

    @Size(max = 20)
    private String modalidad;

    @Size(max = 200)
    private String recursos;

    @Size(max = 100)
    private String motivacion;

    private String linkResumen;

}
