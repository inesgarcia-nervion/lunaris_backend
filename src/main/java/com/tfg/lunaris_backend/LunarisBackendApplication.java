package com.tfg.lunaris_backend;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Clase principal de la aplicación Lunaris Backend.
 * 
 * Esta clase se encarga de iniciar la aplicación Spring Boot.
 */
@SpringBootApplication
public class LunarisBackendApplication {

	/**
	 * Método principal que inicia la aplicación.
	 * @param args argumentos de línea de comandos (no utilizados)
	 */
	public static void main(String[] args) {
		SpringApplication.run(LunarisBackendApplication.class, args);
	}

}
