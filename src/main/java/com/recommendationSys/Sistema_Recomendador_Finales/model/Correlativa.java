package com.recommendationSys.Sistema_Recomendador_Finales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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
    @JoinColumns({
            @JoinColumn(name = "materia_codigo", referencedColumnName = "codigo", nullable = false),
            @JoinColumn(name = "materia_plan_codigo", referencedColumnName = "PlanDeEstudio_codigo", nullable = false)
    })
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Materia materia;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "correlativa_codigo", referencedColumnName = "codigo", nullable = false),
            @JoinColumn(name = "correlativa_plan_codigo", referencedColumnName = "PlanDeEstudio_codigo", nullable = false)
    })
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Materia correlativaCodigo;

}
