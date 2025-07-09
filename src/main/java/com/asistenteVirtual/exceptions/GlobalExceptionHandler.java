package com.asistenteVirtual.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiExceptionResponse> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ApiExceptionResponse(ex.getMessage(), 404), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(PlanEstudioValidationException.class)
    public ResponseEntity<ApiExceptionResponse> handlePlanValidationError(PlanEstudioValidationException ex) {
        return new ResponseEntity<>(new ApiExceptionResponse(ex.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IntegrityException.class)
    public ResponseEntity<ApiExceptionResponse> handleIntegrity(IntegrityException ex) {
        return new ResponseEntity<>(new ApiExceptionResponse(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<ApiExceptionResponse> handleEmail(EmailException ex) {
        return new ResponseEntity<>(new ApiExceptionResponse(ex.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiExceptionResponse> handleGeneral(Exception ex) {
        return new ResponseEntity<>(new ApiExceptionResponse("Unexpected error: " + ex.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PlanIncompatibleException.class)
    public ResponseEntity<ApiExceptionResponse> handlePlanIncompatible(PlanIncompatibleException ex) {
        return new ResponseEntity<>(new ApiExceptionResponse(ex.getMessage(), 500), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnsupportedFileTypeException.class)
    public ResponseEntity<ApiExceptionResponse> handlePlanIncompatible(UnsupportedFileTypeException ex) {
        return new ResponseEntity<>(new ApiExceptionResponse(ex.getMessage(), 400), HttpStatus.BAD_REQUEST);
    }

}
