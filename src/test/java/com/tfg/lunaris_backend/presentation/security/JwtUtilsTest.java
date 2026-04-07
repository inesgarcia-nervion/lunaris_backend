package com.tfg.lunaris_backend.presentation.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilsTest {

    @AfterEach
    void clear() {
        // noop
    }

    @Test
    void generateValidateAndExtract() {
        JwtUtils ju = new JwtUtils();
        ReflectionTestUtils.setField(ju, "jwtSecret", "01234567012345670123456701234567");
        ReflectionTestUtils.setField(ju, "jwtExpirationMs", 3600000L);
        ju.init();

        String token = ju.generateToken("alice", "ADMIN");
        assertNotNull(token);
        assertTrue(ju.validateToken(token));
        assertEquals("alice", ju.getUsernameFromToken(token));

        // also check default generateToken(username)
        String t2 = ju.generateToken("bob");
        assertNotNull(t2);
        assertTrue(ju.validateToken(t2));
        assertEquals("bob", ju.getUsernameFromToken(t2));
    }

    @Test
    void validateToken_invalidToken_returnsFalse() {
        JwtUtils ju = new JwtUtils();
        ReflectionTestUtils.setField(ju, "jwtSecret", "01234567012345670123456701234567");
        ReflectionTestUtils.setField(ju, "jwtExpirationMs", 3600000L);
        ju.init();

        assertFalse(ju.validateToken("not.a.valid.jwt.token"));
        assertFalse(ju.validateToken(""));
    }

    @Test
    void init_shortSecret_usesGeneratedKey() {
        // When jwtSecret has fewer than 32 bytes, a generated key is used instead
        JwtUtils ju = new JwtUtils();
        ReflectionTestUtils.setField(ju, "jwtSecret", "short");
        ReflectionTestUtils.setField(ju, "jwtExpirationMs", 3600000L);
        ju.init(); // should not throw

        String token = ju.generateToken("user");
        assertNotNull(token);
        assertTrue(ju.validateToken(token));
    }
}
