package com.recommendationSys.Sistema_Recomendador_Finales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Materia")
public class Materia {

    @Id
    @Column(length = 15)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanDeEstudio_codigo", nullable = false)
    private PlanDeEstudio planDeEstudio;

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Correlativa> correlativas = new ArrayList<>();

    @OneToMany(mappedBy = "correlativa", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Correlativa> esCorrelavitaDe = new ArrayList<>();

    @OneToMany(mappedBy = "materia")
    @JsonIgnore
    private List<Renglon> renglones;

    @OneToMany(mappedBy = "materia")
    @JsonIgnore
    private List<RegistroInscripcion> inscripciones;

    public Materia() {
    }

    public Materia(String codigo, String nombre, PlanDeEstudio planDeEstudio, List<Correlativa> correlativas, List<Correlativa> esCorrelavitaDe, List<Renglon> renglones, List<RegistroInscripcion> inscripciones) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.planDeEstudio = planDeEstudio;
        this.correlativas = correlativas;
        this.esCorrelavitaDe = esCorrelavitaDe;
        this.renglones = renglones;
        this.inscripciones = inscripciones;
    }


    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public PlanDeEstudio getPlanDeEstudio() {
        return planDeEstudio;
    }

    public void setPlanDeEstudio(PlanDeEstudio planDeEstudio) {
        this.planDeEstudio = planDeEstudio;
    }

    public List<Correlativa> getCorrelativas() {
        return correlativas;
    }

    public void setCorrelativas(List<Correlativa> correlativas) {
        this.correlativas = correlativas;
    }

    public List<Correlativa> getEsCorrelavitaDe() {
        return esCorrelavitaDe;
    }

    public void setEsCorrelavitaDe(List<Correlativa> esCorrelavitaDe) {
        this.esCorrelavitaDe = esCorrelavitaDe;
    }

    public List<Renglon> getRenglones() {
        return renglones;
    }

    public void setRenglones(List<Renglon> renglones) {
        this.renglones = renglones;
    }

    public List<RegistroInscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(List<RegistroInscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }
}

