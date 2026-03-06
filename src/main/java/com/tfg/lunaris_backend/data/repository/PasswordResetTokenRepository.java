package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.PasswordResetToken;
import com.tfg.lunaris_backend.domain.model.User;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    void deleteByUser(User user);
}
