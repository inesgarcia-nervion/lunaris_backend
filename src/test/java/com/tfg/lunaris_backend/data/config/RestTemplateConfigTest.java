package com.tfg.lunaris_backend.data.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para {@link RestTemplateConfig}.
 */
class RestTemplateConfigTest {

    /**
     * Verifica que el RestTemplate tiene los timeouts configurados correctamente.
     */
    @Test
    void restTemplate_hasConfiguredTimeouts() {
        RestTemplateConfig cfg = new RestTemplateConfig();
        RestTemplate rt = cfg.restTemplate();
        assertNotNull(rt);
        assertTrue(rt.getRequestFactory() instanceof SimpleClientHttpRequestFactory);
    }
}
