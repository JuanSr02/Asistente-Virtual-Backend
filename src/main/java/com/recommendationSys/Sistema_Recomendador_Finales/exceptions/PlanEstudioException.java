package com.recommendationSys.Sistema_Recomendador_Finales.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
@Getter
public class PlanEstudioException extends RuntimeException {
    private final HttpStatus status;

    public PlanEstudioException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

}
