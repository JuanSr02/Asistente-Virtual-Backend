package com.recommendationSys.Sistema_Recomendador_Finales.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(String message, String error) {
        super(message + "\n" + error);
    }
}
