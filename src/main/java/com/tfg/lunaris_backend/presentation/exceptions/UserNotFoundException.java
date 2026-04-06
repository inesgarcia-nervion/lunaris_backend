package com.tfg.lunaris_backend.presentation.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un usuario.
 * 
 * Esta excepción se utiliza para indicar que el usuario solicitado no existe en la base de datos.
 * Se lanza principalmente en el servicio de usuarios cuando se intenta acceder a un usuario por su ID y no se encuentra.
 */
public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
