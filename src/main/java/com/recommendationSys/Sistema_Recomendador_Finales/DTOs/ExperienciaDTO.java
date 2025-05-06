package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

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

    @Size(max = 100, message = "Las condiciones no pueden exceder los 100 caracteres")
    private String condiciones;

    // Getters y Setters

    public ExperienciaDTO() {
    }

    public ExperienciaDTO(Long examenId, Integer dificultad, Integer diasEstudio, Integer horasDiarias, Integer intentosPrevios, String modalidad, String recursos, String motivacion, String condiciones) {
        this.examenId = examenId;
        this.dificultad = dificultad;
        this.diasEstudio = diasEstudio;
        this.horasDiarias = horasDiarias;
        this.intentosPrevios = intentosPrevios;
        this.modalidad = modalidad;
        this.recursos = recursos;
        this.motivacion = motivacion;
        this.condiciones = condiciones;
    }


    public Long getExamenId() {
        return examenId;
    }

    public void setExamenId(Long examenId) {
        this.examenId = examenId;
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
