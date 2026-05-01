package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase PasswordResetRequest.
 */
class PasswordResetRequestTest {

    /**
     * Verifica que se puede establecer y obtener el correo electrónico.
     */
    @Test
    void emailField() {
        PasswordResetRequest r = new PasswordResetRequest();
        r.setEmail("a@b.com");
        assertEquals("a@b.com", r.getEmail());
    }
}
