package com.ln.fitness.ai_service;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AiServiceApplication {

	public static void main(String[] args) {
// Load .env file manually
		Dotenv dotenv = Dotenv.load();

		// Set env vars as system properties so Spring can pick them up
		dotenv.entries().forEach(entry -> {
			if (System.getProperty(entry.getKey()) == null) { // avoid overriding
				System.setProperty(entry.getKey(), entry.getValue());
			}
		});
		SpringApplication.run(AiServiceApplication.class, args);
	}

}
