package com.tfg.lunaris_backend.data.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Increase timeouts to handle slower external API responses (OpenLibrary can be
        // slow)
        factory.setConnectTimeout(5000); // 5s connect
        factory.setReadTimeout(15000); // 15s read
        return new RestTemplate(factory);
    }
}
