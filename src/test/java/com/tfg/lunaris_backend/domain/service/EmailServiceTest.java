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

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService svc;

    private MimeMessage mime;

    @BeforeEach
    void setup() {
        mime = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mime);
        // set private fields
        org.springframework.test.util.ReflectionTestUtils.setField(svc, "fromEmail", "from@x.com");
        org.springframework.test.util.ReflectionTestUtils.setField(svc, "frontendUrl", "https://app" );
    }

    @Test
    void sendPasswordResetEmail_callsMailSender() throws Exception {
        svc.sendPasswordResetEmail("to@x.com", "token123");
        verify(mailSender).send(any(MimeMessage.class));
    }
}
