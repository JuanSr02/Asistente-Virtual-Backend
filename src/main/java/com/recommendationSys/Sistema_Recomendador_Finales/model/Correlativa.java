package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Correlativa")
@IdClass(CorrelativaId.class)
public class Correlativa {

    @Id
    @ManyToOne
    @JoinColumn(name = "materia_codigo")
    private Materia materia;

    @Id
    @ManyToOne
    @JoinColumn(name = "correlativa_codigo")
    private Materia correlativa;

    @Id
    @ManyToOne
    @JoinColumn(name = "PlanDeEstudio_codigo")
    private PlanDeEstudio planDeEstudio;
}


