package com.recommendationSys.Sistema_Recomendador_Finales.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(String message,String error) {
        System.out.println(message+"\n"+error);
    }
}
