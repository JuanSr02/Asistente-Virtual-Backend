package com.asistenteVirtual.common.exceptions;

// En una nueva clase, por ejemplo en el paquete 'exceptions'
public class PlanIncompatibleException extends RuntimeException {
    public PlanIncompatibleException(String message) {
        super(message);
    }
}