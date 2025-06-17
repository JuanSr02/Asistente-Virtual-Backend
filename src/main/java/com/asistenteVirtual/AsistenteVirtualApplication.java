package com.asistenteVirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal del sistema recomendador de finales.
 * Inicia la aplicación Spring Boot.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.asistenteVirtual.model")
@EnableJpaRepositories(basePackages = "com.asistenteVirtual.repository")
public class AsistenteVirtualApplication {

    public static void main(String[] args) {
        SpringApplication.run(AsistenteVirtualApplication.class, args);
    }
}
