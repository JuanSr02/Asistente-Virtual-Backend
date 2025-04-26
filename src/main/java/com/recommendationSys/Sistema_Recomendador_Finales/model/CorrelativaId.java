package com.recommendationSys.Sistema_Recomendador_Finales.model;

import lombok.Data;
import java.io.Serializable;
import java.util.Objects;

import java.io.Serializable;
import java.util.Objects;

public class CorrelativaId implements Serializable {
    private String materia;
    private String correlativa;
    private String planDeEstudio;

    // Equals & hashCode

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CorrelativaId that = (CorrelativaId) o;
        return Objects.equals(materia, that.materia) && Objects.equals(correlativa, that.correlativa) && Objects.equals(planDeEstudio, that.planDeEstudio);
    }

    @Override
    public int hashCode() {
        return Objects.hash(materia, correlativa, planDeEstudio);
    }
}
