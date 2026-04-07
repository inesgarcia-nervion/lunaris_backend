package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthRequestTest {

    @Test
    void userAndPassword() {
        AuthRequest r = new AuthRequest();
        r.setUsername("u");
        r.setPassword("p");
        assertEquals("u", r.getUsername());
        assertEquals("p", r.getPassword());
    }
}
