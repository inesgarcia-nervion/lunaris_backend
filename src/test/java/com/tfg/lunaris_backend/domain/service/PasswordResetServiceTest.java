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

    @Test
    void requestPasswordReset_emailNotFound() {
        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.empty());
        assertEquals("EMAIL_NOT_FOUND", svc.requestPasswordReset("a@b.com"));
    }

    @Test
    void requestPasswordReset_successAndEmailError() throws MessagingException {
        User u = new User(); u.setEmail("a@b.com");
        when(userRepo.findByEmail("a@b.com")).thenReturn(Optional.of(u));

        // success
        doNothing().when(emailService).sendPasswordResetEmail(eq("a@b.com"), anyString());
        assertEquals("SUCCESS", svc.requestPasswordReset("a@b.com"));

        // email error
        doThrow(new MessagingException("e")).when(emailService).sendPasswordResetEmail(eq("a@b.com"), anyString());
        assertEquals("EMAIL_ERROR", svc.requestPasswordReset("a@b.com"));
    }

    @Test
    void validateTokenAndResetPasswordFlows() {
        User u = new User(); u.setUsername("u");
        PasswordResetToken t = new PasswordResetToken("tok", u);
        when(tokenRepo.findByToken("tok")).thenReturn(Optional.of(t));
        assertTrue(svc.validateToken("tok"));

        when(tokenRepo.findByToken("bad")).thenReturn(Optional.empty());
        assertFalse(svc.validateToken("bad"));

        // reset password success
        when(tokenRepo.findByToken("tok2")).thenReturn(Optional.of(t));
        when(encoder.encode("np")).thenReturn("enc");
        when(userRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(tokenRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        t.setUsed(false);
        t.setExpiryDate(java.time.LocalDateTime.now().plusHours(1));
        assertTrue(svc.resetPassword("tok2", "np"));

        // token not found
        assertFalse(svc.resetPassword("xxx", "np"));
    }

    @Test
    void validateToken_expiredToken_returnsFalse() {
        User u = new User();
        PasswordResetToken t = new PasswordResetToken("expired", u);
        t.setExpiryDate(java.time.LocalDateTime.now().minusHours(2)); // already expired
        when(tokenRepo.findByToken("expired")).thenReturn(Optional.of(t));
        assertFalse(svc.validateToken("expired"));
    }

    @Test
    void validateToken_usedToken_returnsFalse() {
        User u = new User();
        PasswordResetToken t = new PasswordResetToken("used", u);
        t.setUsed(true);
        when(tokenRepo.findByToken("used")).thenReturn(Optional.of(t));
        assertFalse(svc.validateToken("used"));
    }

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
