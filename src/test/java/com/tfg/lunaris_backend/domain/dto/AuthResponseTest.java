package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AuthResponseTest {

    @Test
    void constructorSetsToken() {
        AuthResponse r = new AuthResponse("tk");
        assertEquals("tk", r.getToken());
    }
}
