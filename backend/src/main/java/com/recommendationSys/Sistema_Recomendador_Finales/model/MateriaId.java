package com.recommendationSys.Sistema_Recomendador_Finales.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class MateriaId implements Serializable {
    private String codigo;
    private PlanDeEstudio planDeEstudio;
}