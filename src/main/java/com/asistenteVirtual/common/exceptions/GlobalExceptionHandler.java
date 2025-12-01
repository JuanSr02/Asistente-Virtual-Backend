package com.asistenteVirtual.common.exceptions;

import com.asistenteVirtual.common.exceptions.dto.ApiErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IntegrityException.class, UnsupportedFileTypeException.class})
    public ResponseEntity<ApiErrorResponse> handleBadRequest(RuntimeException ex) {
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            PlanEstudioValidationException.class,
            EmailException.class,
            PlanIncompatibleException.class
    })
    public ResponseEntity<ApiErrorResponse> handleBusinessLogicError(RuntimeException ex) {
        log.error("Error de lógica de negocio o validación interna: ", ex);
        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(Exception ex) {
        log.error("Error inesperado no controlado: ", ex);
        return buildResponse("Ocurrió un error interno inesperado. Contacte al soporte.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Metodo helper privado para evitar repetir código (DRY)
    private ResponseEntity<ApiErrorResponse> buildResponse(String message, HttpStatus status) {
        return new ResponseEntity<>(new ApiErrorResponse(message, status.value()), status);
    }
}