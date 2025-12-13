package com.asistenteVirtual.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.reglas-academicas")
public class AcademicProperties {

    /**
     * Cantidad de años que dura la regularidad de una materia.
     * Valor por defecto según ordenanza actual: 2 años.
     */
    private int regularidadAnios = 2;

    /**
     * Cantidad de meses adicionales que dura la regularidad.
     * Valor por defecto: 9 meses.
     */
    private int regularidadMeses = 9;
}