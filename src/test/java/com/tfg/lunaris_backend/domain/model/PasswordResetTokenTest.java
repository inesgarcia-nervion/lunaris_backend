package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

/**
 * Test para la clase PasswordResetToken.
 */
class PasswordResetTokenTest {

    /**
     * Verifica que el constructor establece correctamente la fecha de expiración y el usuario.
     */
    @Test
    void constructorSetsExpiryAndUser() {
        User u = new User();
        u.setUsername("user");
        PasswordResetToken t = new PasswordResetToken("tok", u);

        assertEquals("tok", t.getToken());
        assertSame(u, t.getUser());
        assertNotNull(t.getExpiryDate());
        assertFalse(t.isExpired());
    }

    /**
     * Verifica que el método isExpired devuelve true cuando la fecha de expiración está en el pasado.
     */
    @Test
    void expiredWhenDateInPast() {
        User u = new User();
        PasswordResetToken t = new PasswordResetToken();
        t.setToken("x");
        t.setUser(u);
        t.setExpiryDate(LocalDateTime.now().minusHours(2));
        assertTrue(t.isExpired());
    }
}
