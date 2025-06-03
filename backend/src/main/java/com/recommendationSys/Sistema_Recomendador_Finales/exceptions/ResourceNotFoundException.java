package com.recommendationSys.Sistema_Recomendador_Finales.exceptions;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}