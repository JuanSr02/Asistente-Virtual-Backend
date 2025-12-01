package com.asistenteVirtual.common.exceptions;

public class EmailException extends RuntimeException {
    public EmailException(String message, String error) {
        super(message + "\n" + error);
    }
}
