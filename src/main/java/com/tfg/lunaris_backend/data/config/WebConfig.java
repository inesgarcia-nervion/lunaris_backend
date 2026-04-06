package com.tfg.lunaris_backend.data.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración MVC para la aplicación.
 *
 * Configura CORS para permitir peticiones desde el frontend en desarrollo.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Añade las rutas CORS permitidas.
     *
     * @param registry registro de CORS proporcionado por Spring MVC
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:4200", "http://127.0.0.1:4200")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
