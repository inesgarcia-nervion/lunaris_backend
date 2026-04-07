package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NewPasswordRequestTest {

    @Test
    void tokenAndNewPassword() {
        NewPasswordRequest r = new NewPasswordRequest();
        r.setToken("t");
        r.setNewPassword("np");
        assertEquals("t", r.getToken());
        assertEquals("np", r.getNewPassword());
    }
}
