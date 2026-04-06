package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;

/**
 * DTO para solicitudes de autenticación.
 *
 * Contiene el nombre de usuario y la contraseña proporcionados por el cliente durante el proceso de inicio de sesión.
 */
@Data
public class AuthRequest {
    private String username;
    private String password;
}
