package com.recommendationSys.Sistema_Recomendador_Finales.exceptions;

import lombok.Getter;
@Getter
public class PlanEstudioValidationException extends RuntimeException {

    public PlanEstudioValidationException(String message) {
        super(message);
    }

}
