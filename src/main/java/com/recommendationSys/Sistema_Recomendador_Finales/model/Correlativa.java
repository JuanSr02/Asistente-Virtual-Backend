package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table(name = "Correlativa")
@Entity
@IdClass(CorrelativaId.class)
public class Correlativa {

    @Id
    @ManyToOne
    @JoinColumn(name = "materia_codigo", nullable = false)
    private Materia materia;

    @Id
    @ManyToOne
    @JoinColumn(name = "correlativa_codigo", nullable = false)
    private Materia correlativa;

    @Id
    @ManyToOne
    @JoinColumn(name = "PlanDeEstudio_codigo", nullable = false)
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


