package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordResetRequestTest {

    @Test
    void emailField() {
        PasswordResetRequest r = new PasswordResetRequest();
        r.setEmail("a@b.com");
        assertEquals("a@b.com", r.getEmail());
    }
}
