package com.asistenteVirtual;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal del Asistente virtual.
 * Inicia la aplicación Spring Boot.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.asistenteVirtual.model")
@EnableJpaRepositories(basePackages = "com.asistenteVirtual.repository")
public class AsistenteVirtualApplication {

    public static void main(String[] args) {
        System.out.println("DB_URL = " + System.getenv("DB_URL"));
        System.out.println("DB_USER = " + System.getenv("DB_USER"));
        System.out.println("DB_PASS = " + System.getenv("DB_PASS"));
        SpringApplication.run(AsistenteVirtualApplication.class, args);
    }
}
