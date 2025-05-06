package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Experiencia")
public class Experiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "examen_id", unique = true)
    private Examen examen;

    @Column(nullable = false)
    private Integer dificultad;

    @Column(name = "dias_estudio", nullable = false)
    private Integer diasEstudio;

    @Column(name = "horas_diarias", nullable = false)
    private Integer horasDiarias;

    @Column(name = "intentos_previos", nullable = false)
    private Integer intentosPrevios;

    @Column(length = 20)
    private String modalidad;

    @Column(length = 200)
    private String recursos;

    @Column(length = 100)
    private String motivacion;

    @Column(length = 100)
    private String condiciones;

    public Experiencia() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Examen getExamen() {
        return examen;
    }

    public void setExamen(Examen examen) {
        this.examen = examen;
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