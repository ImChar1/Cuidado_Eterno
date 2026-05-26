package com.cuidadoeterno.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.cuidadoeterno.backend.config.JwtConfig;

/**
 * Punto de entrada de la aplicación Spring Boot.
 *
 * @EnableConfigurationProperties habilita el mapeo de application.yml
 * hacia JwtConfig usando @ConfigurationProperties.
 */

@SpringBootApplication
@EnableConfigurationProperties(JwtConfig.class)
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
