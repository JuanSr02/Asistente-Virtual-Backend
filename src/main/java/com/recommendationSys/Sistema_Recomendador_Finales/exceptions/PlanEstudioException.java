package com.recommendationSys.Sistema_Recomendador_Finales.exceptions;

import org.springframework.http.HttpStatus;

public class PlanEstudioException extends RuntimeException {
    private final HttpStatus status;

    public PlanEstudioException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
