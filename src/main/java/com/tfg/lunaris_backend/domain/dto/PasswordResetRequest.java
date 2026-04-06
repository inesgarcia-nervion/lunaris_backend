package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;

/**
 * DTO para solicitudes de restablecimiento de contraseña.
 * 
 * Contiene el correo electrónico del usuario que ha solicitado el restablecimiento de contraseña. 
 * Este correo se utilizará para enviar un enlace con un token de restablecimiento al usuario.
 */
@Data
public class PasswordResetRequest {
    private String email;
}
