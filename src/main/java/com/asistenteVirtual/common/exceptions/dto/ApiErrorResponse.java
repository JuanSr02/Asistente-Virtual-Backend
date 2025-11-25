package com.asistenteVirtual.common.exceptions.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * Record inmutable para respuestas de error estandarizadas.
 * Agregamos 'timestamp' para mejor trazabilidad.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorResponse(
        String message,
        int status,
        LocalDateTime timestamp
) {
    // Constructor compacto para facilitar uso
    public ApiErrorResponse(String message, int status) {
        this(message, status, LocalDateTime.now());
    }
}