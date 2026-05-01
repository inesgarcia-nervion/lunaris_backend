package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.PasswordResetTokenRepository;
import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.model.PasswordResetToken;
import com.tfg.lunaris_backend.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import jakarta.mail.MessagingException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para la clase PasswordResetService.
 */
@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepo;

    @Mock
    private PasswordResetTokenRepository tokenRepo;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private PasswordResetService svc;

    /**
     * Verifica que se lanza una excepción cuando no se encuentra un correo electrónico.
     */
    @Test
    void requestPasswordReset_emailNotFound() {
        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.empty());
        assertEquals("EMAIL_NOT_FOUND", svc.requestPasswordReset("a@b.com"));
    }

    /**
     * Verifica el flujo exitoso y el manejo de errores al enviar el correo electrónico.
     */
    @Test
    void requestPasswordReset_successAndEmailError() throws MessagingException {
        User u = new User(); u.setEmail("a@b.com");
        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.of(u));

        doNothing().when(emailService).sendPasswordResetEmail(eq("a@b.com"), anyString());
        assertEquals("SUCCESS", svc.requestPasswordReset("a@b.com"));

        doThrow(new MessagingException("e")).when(emailService).sendPasswordResetEmail(eq("a@b.com"), anyString());
        assertEquals("EMAIL_ERROR", svc.requestPasswordReset("a@b.com"));
    }

    /**
     * Verifica los flujos de validación de tokens y restablecimiento de contraseñas, incluyendo casos de éxito y errores.
     */
    @Test
    void validateTokenAndResetPasswordFlows() {
        User u = new User(); u.setUsername("u");
        PasswordResetToken t = new PasswordResetToken("tok", u);
        when(tokenRepo.findByToken("tok")).thenReturn(Optional.of(t));
        assertTrue(svc.validateToken("tok"));

        when(tokenRepo.findByToken("bad")).thenReturn(Optional.empty());
        assertFalse(svc.validateToken("bad"));

        when(tokenRepo.findByToken("tok2")).thenReturn(Optional.of(t));
        when(encoder.encode("np")).thenReturn("enc");
        when(userRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(tokenRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        t.setUsed(false);
        t.setExpiryDate(java.time.LocalDateTime.now().plusHours(1));
        assertTrue(svc.resetPassword("tok2", "np"));

        assertFalse(svc.resetPassword("xxx", "np"));
    }

    /**
     * Verifica que se retorna falso cuando el token ha expirado.
     */
    @Test
    void validateToken_expiredToken_returnsFalse() {
        User u = new User();
        PasswordResetToken t = new PasswordResetToken("expired", u);
        t.setExpiryDate(java.time.LocalDateTime.now().minusHours(2)); 
        when(tokenRepo.findByToken("expired")).thenReturn(Optional.of(t));
        assertFalse(svc.validateToken("expired"));
    }

    /**
     * Verifica que se retorna falso cuando el token ya ha sido usado.
     */
    @Test
    void validateToken_usedToken_returnsFalse() {
        User u = new User();
        PasswordResetToken t = new PasswordResetToken("used", u);
        t.setUsed(true);
        when(tokenRepo.findByToken("used")).thenReturn(Optional.of(t));
        assertFalse(svc.validateToken("used"));
    }

    /**
     * Verifica que se retorna falso cuando se intenta restablecer la contraseña con un token expirado o ya usado.
     */
    @Test
    void resetPassword_expiredOrUsedToken_returnsFalse() {
        User u = new User();
        PasswordResetToken expired = new PasswordResetToken("tok3", u);
        expired.setExpiryDate(java.time.LocalDateTime.now().minusHours(1));
        when(tokenRepo.findByToken("tok3")).thenReturn(Optional.of(expired));
        assertFalse(svc.resetPassword("tok3", "newpass"));

        PasswordResetToken used = new PasswordResetToken("tok4", u);
        used.setUsed(true);
        when(tokenRepo.findByToken("tok4")).thenReturn(Optional.of(used));
        assertFalse(svc.resetPassword("tok4", "newpass"));
    }
}
