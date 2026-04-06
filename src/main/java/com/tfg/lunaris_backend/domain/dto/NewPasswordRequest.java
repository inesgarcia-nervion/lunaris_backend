package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;

/**
 * DTO para enviar una nueva contraseña junto al token de restablecimiento.
 * 
 * Contiene el token de restablecimiento de contraseña recibido por correo electrónico 
 * y la nueva contraseña que el usuario desea establecer.
 */
@Data
public class NewPasswordRequest {
    private String token;
    private String newPassword;
}
