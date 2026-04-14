package com.tfg.lunaris_backend.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.Session;
import java.util.Properties;

import static org.mockito.Mockito.*;

/**
 * Test para la clase EmailService.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService svc;

    private MimeMessage mime;

    /**
     * Configura el entorno antes de cada prueba.
     */
    @BeforeEach
    void setup() {
        mime = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mime);
        org.springframework.test.util.ReflectionTestUtils.setField(svc, "fromEmail", "from@x.com");
        org.springframework.test.util.ReflectionTestUtils.setField(svc, "frontendUrl", "https://app" );
    }

    /**
     * Verifica que se llama al mail sender para enviar el correo de restablecimiento de contraseña.
     */
    @Test
    void sendPasswordResetEmail_callsMailSender() throws Exception {
        svc.sendPasswordResetEmail("to@x.com", "token123");
        verify(mailSender).send(any(MimeMessage.class));
    }
}
