package com.tfg.lunaris_backend.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Configuración para `RestTemplate` usada en llamadas HTTP salientes.
 */
@Configuration
public class RestTemplateConfig {

    /**
     * Crea un `RestTemplate` con tiempos de espera adecuados para conexiones externas.
     *
     * @return instancia de `RestTemplate` configurada
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); 
        factory.setReadTimeout(15000); 
        return new RestTemplate(factory);
    }
}
