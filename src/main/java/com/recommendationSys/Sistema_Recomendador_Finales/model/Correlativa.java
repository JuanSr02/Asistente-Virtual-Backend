package com.recommendationSys.Sistema_Recomendador_Finales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "Correlativa")
@Entity
@IdClass(CorrelativaId.class)
public class Correlativa {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "materia_codigo", nullable = false)
    @JsonIgnore
    private Materia materia;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correlativa_codigo", nullable = false)
    @JsonIgnore
    private Materia correlativa;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanDeEstudio_codigo", nullable = false)
    @JsonIgnore
    private PlanDeEstudio planDeEstudio;

    // Getters, Setters, Constructor

    public Correlativa() {
    }

    public Correlativa(Materia materia, Materia correlativa, PlanDeEstudio planDeEstudio) {
        this.materia = materia;
        this.correlativa = correlativa;
        this.planDeEstudio = planDeEstudio;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Materia getCorrelativa() {
        return correlativa;
    }

    public void setCorrelativa(Materia correlativa) {
        this.correlativa = correlativa;
    }

    public PlanDeEstudio getPlanDeEstudio() {
        return planDeEstudio;
    }

    public void setPlanDeEstudio(PlanDeEstudio planDeEstudio) {
        this.planDeEstudio = planDeEstudio;
    }
}


