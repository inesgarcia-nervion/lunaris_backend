package com.tfg.lunaris_backend.data.config;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    void addCorsMappings_callsAddMapping() {
        WebConfig cfg = new WebConfig();
        CorsRegistry registry = Mockito.mock(CorsRegistry.class);
        CorsRegistration registration = Mockito.mock(CorsRegistration.class);

        when(registry.addMapping("/**")).thenReturn(registration);
        when(registration.allowedOrigins((String[]) Mockito.any(String[].class))).thenReturn(registration);
        when(registration.allowedMethods((String[]) Mockito.any(String[].class))).thenReturn(registration);
        when(registration.allowedHeaders((String[]) Mockito.any(String[].class))).thenReturn(registration);
        when(registration.allowCredentials(anyBoolean())).thenReturn(registration);
        when(registration.maxAge(anyLong())).thenReturn(registration);

        cfg.addCorsMappings(registry);

        verify(registry, times(1)).addMapping("/**");
    }
}
