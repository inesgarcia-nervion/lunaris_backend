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

/**
 * Servicio que maneja la lógica de negocio relacionada con el restablecimiento de contraseñas.
 * 
 * Proporciona métodos para solicitar un restablecimiento de contraseña, validar tokens y restablecer contraseñas.
 */
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

    /**
     * Solicita un restablecimiento de contraseña para el usuario con el correo electrónico proporcionado.
     * @param email correo electrónico del usuario
     * @return estado de la solicitud ("SUCCESS", "EMAIL_NOT_FOUND", "EMAIL_ERROR")
     */
    @Transactional
    public String requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "EMAIL_NOT_FOUND";
        }

        User user = userOpt.get();

        tokenRepository.deleteByUser(user);

        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = new PasswordResetToken(token, user);
        tokenRepository.save(resetToken);

        try {
            emailService.sendPasswordResetEmail(email, token);
            return "SUCCESS";
        } catch (MessagingException e) {
            e.printStackTrace();
            return "EMAIL_ERROR";
        }
    }

    /**
     * Valida un token de restablecimiento de contraseña.
     * @param token token a validar
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        return !resetToken.isExpired() && !resetToken.isUsed();
    }

    /**
     * Restablece la contraseña del usuario utilizando un token válido.
     * @param token token de restablecimiento de contraseña
     * @param newPassword nueva contraseña del usuario
     * @return true si la contraseña se restablece correctamente, false en caso contrario
     */
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

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return true;
    }
}
