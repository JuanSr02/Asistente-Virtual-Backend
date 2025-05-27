package com.recommendationSys.Sistema_Recomendador_Finales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "correlativa")
@IdClass(CorrelativaId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    private Materia correlativaCodigo;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanDeEstudio_codigo", nullable = false)
    @JsonIgnore
    private PlanDeEstudio planDeEstudio;

}
