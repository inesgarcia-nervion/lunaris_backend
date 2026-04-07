package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void properties() {
        User u = new User();
        u.setUsername("usr");
        u.setEmail("e@x.com");
        u.setPassword("pwd");
        u.setRole("ROLE_USER");

        assertEquals("usr", u.getUsername());
        assertEquals("e@x.com", u.getEmail());
        assertEquals("pwd", u.getPassword());
        assertEquals("ROLE_USER", u.getRole());
    }
}
