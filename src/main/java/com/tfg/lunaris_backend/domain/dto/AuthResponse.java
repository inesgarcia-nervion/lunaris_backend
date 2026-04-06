package com.tfg.lunaris_backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO para respuestas de autenticación.
 *
 * Contiene el token JWT generado después de una autenticación exitosa, que el cliente utilizará para acceder a recursos protegidos.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
}
