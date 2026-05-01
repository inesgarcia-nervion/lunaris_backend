package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase AuthRequest.
 */
class AuthRequestTest {

    /**
     * Verifica que se pueden establecer y obtener el nombre de usuario y la contraseña.
     */
    @Test
    void userAndPassword() {
        AuthRequest r = new AuthRequest();
        r.setUsername("u");
        r.setPassword("p");
        assertEquals("u", r.getUsername());
        assertEquals("p", r.getPassword());
    }
}
