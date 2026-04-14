package com.tfg.lunaris_backend.presentation.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para {@link JwtUtils}.
 */
class JwtUtilsTest {

    @AfterEach
    void clear() {
    }

    /**
     * Verifica que se pueden generar, validar y extraer correctamente los tokens JWT.
     */
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

        String t2 = ju.generateToken("bob");
        assertNotNull(t2);
        assertTrue(ju.validateToken(t2));
        assertEquals("bob", ju.getUsernameFromToken(t2));
    }

    /**
     * Verifica que un token inválido devuelve false al validar.
     */
    @Test
    void validateToken_invalidToken_returnsFalse() {
        JwtUtils ju = new JwtUtils();
        ReflectionTestUtils.setField(ju, "jwtSecret", "01234567012345670123456701234567");
        ReflectionTestUtils.setField(ju, "jwtExpirationMs", 3600000L);
        ju.init();

        assertFalse(ju.validateToken("not.a.valid.jwt.token"));
        assertFalse(ju.validateToken(""));
    }

    /**
     * Verifica que si la clave secreta es demasiado corta, se utiliza una clave generada en su lugar.
     */
    @Test
    void init_shortSecret_usesGeneratedKey() {
        JwtUtils ju = new JwtUtils();
        ReflectionTestUtils.setField(ju, "jwtSecret", "short");
        ReflectionTestUtils.setField(ju, "jwtExpirationMs", 3600000L);
        ju.init(); 

        String token = ju.generateToken("user");
        assertNotNull(token);
        assertTrue(ju.validateToken(token));
    }
}
