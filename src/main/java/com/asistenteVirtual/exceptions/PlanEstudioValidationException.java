package com.asistenteVirtual.exceptions;

import lombok.Getter;
@Getter
public class PlanEstudioValidationException extends RuntimeException {

    public PlanEstudioValidationException(String message) {
        super(message);
    }

}
