package com.recommendationSys.Sistema_Recomendador_Finales;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "repository")
public class SistemaRecomendadorDeFinalesApplication {
	public static void main(String[] args) {
		SpringApplication.run(SistemaRecomendadorDeFinalesApplication.class, args);
	}
}