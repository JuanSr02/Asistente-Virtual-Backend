package com.recommendationSys.Sistema_Recomendador_Finales.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class CorrelativaId implements Serializable {
    private String materia;
    private String correlativaCodigo;
    private String planDeEstudio;
}
