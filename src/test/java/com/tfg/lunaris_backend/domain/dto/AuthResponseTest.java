package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase AuthResponse.
 */
class AuthResponseTest {

    /**
     * Verifica que el constructor establece el token correctamente.
     */
    @Test
    void constructorSetsToken() {
        AuthResponse r = new AuthResponse("tk");
        assertEquals("tk", r.getToken());
    }
}
