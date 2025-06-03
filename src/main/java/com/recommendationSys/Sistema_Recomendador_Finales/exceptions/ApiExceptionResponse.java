package com.recommendationSys.Sistema_Recomendador_Finales.exceptions;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApiExceptionResponse {
    private String message;
    private int status;

    public ApiExceptionResponse(String message, int status) {
        this.message = message;
        this.status = status;
    }

}
