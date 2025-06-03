package com.recommendationSys.Sistema_Recomendador_Finales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Clase principal del sistema recomendador de finales.
 * Inicia la aplicación Spring Boot.
 */
@SpringBootApplication
@EntityScan(basePackages = "com.recommendationSys.Sistema_Recomendador_Finales.model")
@EnableJpaRepositories(basePackages = "com.recommendationSys.Sistema_Recomendador_Finales.repository")
public class SistemaRecomendadorDeFinalesApplication {

	public static void main(String[] args) {
		SpringApplication.run(SistemaRecomendadorDeFinalesApplication.class, args);
	}
}
