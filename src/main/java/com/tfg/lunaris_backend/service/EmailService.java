package com.tfg.lunaris_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String toEmail, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("Lunaris - Recuperación de contraseña");

        String resetLink = frontendUrl + "/reset-password?token=" + token;

        String htmlContent = """
                <!DOCTYPE html>
                <html>
                <head>
                    <style>
                        body { font-family: 'Poppins', Arial, sans-serif; background-color: #fafafa; padding: 20px; }
                        .container { max-width: 600px; margin: 0 auto; background: white; border-radius: 12px; padding: 40px; box-shadow: 0 4px 20px rgba(0,0,0,0.1); }
                        .logo { text-align: center; margin-bottom: 20px; }
                        .logo img { width: 100px; }
                        h1 { color: #4e2a6a; text-align: center; }
                        p { color: #555; line-height: 1.6; }
                        .button { display: block; width: fit-content; margin: 30px auto; padding: 15px 30px; background: linear-gradient(90deg, #b48bff, #b18bff); color: white; text-decoration: none; border-radius: 10px; font-weight: bold; }
                        .footer { margin-top: 30px; text-align: center; color: #999; font-size: 12px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <h1>🌙 Lunaris</h1>
                        <h2 style="text-align: center; color: #caa6ff;">Recuperación de contraseña</h2>
                        <p>Hola,</p>
                        <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en Lunaris.</p>
                        <p>Haz clic en el siguiente botón para crear una nueva contraseña:</p>
                        <a href="%s" class="button">Restablecer contraseña</a>
                        <p>Si no solicitaste este cambio, puedes ignorar este correo. Tu contraseña seguirá siendo la misma.</p>
                        <p>Este enlace expirará en <strong>1 hora</strong>.</p>
                        <div class="footer">
                            <p>© 2026 Lunaris - Tu biblioteca personal</p>
                        </div>
                    </div>
                </body>
                </html>
                """
                .formatted(resetLink);

        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
}
