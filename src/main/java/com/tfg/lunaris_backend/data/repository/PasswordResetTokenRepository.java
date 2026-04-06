package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.PasswordResetToken;
import com.tfg.lunaris_backend.domain.model.User;

import java.util.Optional;

/**
 * Repositorio para tokens de restablecimiento de contraseña.
 *
 * Proporciona búsqueda por token y eliminación por usuario.
 */
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    /**
     * Busca un token de restablecimiento por su valor.
     *
     * @param token valor del token
     * @return optional con el token si existe
     */
    Optional<PasswordResetToken> findByToken(String token);

    /**
     * Elimina el token asociado a un usuario.
     *
     * @param user usuario cuyo token debe eliminarse
     */
    void deleteByUser(User user);
}
