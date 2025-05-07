package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class ActualizarExperienciaDTO {
    @Min(1) @Max(10)
    private Integer dificultad;

    @Min(0)
    private Integer diasEstudio;

    @Min(0) @Max(24)
    private Integer horasDiarias;

    @Min(0)
    private Integer intentosPrevios;

    @Size(max = 20)
    private String modalidad;

    @Size(max = 200)
    private String recursos;

    @Size(max = 100)
    private String motivacion;

    @Size(max = 100)
    private String condiciones;

    // Getters y Setters


    public ActualizarExperienciaDTO(Integer dificultad, Integer diasEstudio, Integer horasDiarias, Integer intentosPrevios, String modalidad, String recursos, String motivacion, String condiciones) {
        this.dificultad = dificultad;
        this.diasEstudio = diasEstudio;
        this.horasDiarias = horasDiarias;
        this.intentosPrevios = intentosPrevios;
        this.modalidad = modalidad;
        this.recursos = recursos;
        this.motivacion = motivacion;
        this.condiciones = condiciones;
    }

    public ActualizarExperienciaDTO() {
    }

    public Integer getDificultad() {
        return dificultad;
    }

    public void setDificultad(Integer dificultad) {
        this.dificultad = dificultad;
    }

    public Integer getDiasEstudio() {
        return diasEstudio;
    }

    public void setDiasEstudio(Integer diasEstudio) {
        this.diasEstudio = diasEstudio;
    }

    public Integer getHorasDiarias() {
        return horasDiarias;
    }

    public void setHorasDiarias(Integer horasDiarias) {
        this.horasDiarias = horasDiarias;
    }

    public Integer getIntentosPrevios() {
        return intentosPrevios;
    }

    public void setIntentosPrevios(Integer intentosPrevios) {
        this.intentosPrevios = intentosPrevios;
    }

    public String getModalidad() {
        return modalidad;
    }

    public void setModalidad(String modalidad) {
        this.modalidad = modalidad;
    }

    public String getRecursos() {
        return recursos;
    }

    public void setRecursos(String recursos) {
        this.recursos = recursos;
    }

    public String getMotivacion() {
        return motivacion;
    }

    public void setMotivacion(String motivacion) {
        this.motivacion = motivacion;
    }

    public String getCondiciones() {
        return condiciones;
    }

    public void setCondiciones(String condiciones) {
        this.condiciones = condiciones;
    }
}
