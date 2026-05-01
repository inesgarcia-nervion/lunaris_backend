package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase NewPasswordRequest.
 */
class NewPasswordRequestTest {

    /**
     * Verifica que se pueden establecer y obtener el token y la nueva contraseña.
     */
    @Test
    void tokenAndNewPassword() {
        NewPasswordRequest r = new NewPasswordRequest();
        r.setToken("t");
        r.setNewPassword("np");
        assertEquals("t", r.getToken());
        assertEquals("np", r.getNewPassword());
    }
}
