package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.PasswordResetTokenRepository;
import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.model.PasswordResetToken;
import com.tfg.lunaris_backend.domain.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.mail.MessagingException;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public String requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "EMAIL_NOT_FOUND";
        }

        User user = userOpt.get();

        // Eliminar tokens anteriores del usuario
        tokenRepository.deleteByUser(user);

        // Crear nuevo token
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        // Enviar email
        try {
            emailService.sendPasswordResetEmail(email, token);
            return "SUCCESS";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "EMAIL_ERROR";
        }
    }

    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        return !resetToken.isExpired() && !resetToken.isUsed();
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isExpired() || resetToken.isUsed()) {
            return false;
        }

        // Actualizar contraseña
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Marcar token como usado
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }
}
