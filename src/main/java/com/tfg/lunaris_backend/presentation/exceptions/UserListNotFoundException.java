package com.tfg.lunaris_backend.presentation.exceptions;

/**
 * Excepción lanzada cuando no se encuentra una lista de usuarios.
 * 
 * Esta excepción se utiliza para indicar que la lista de usuarios solicitada no existe en la base de datos.
 * Se lanza principalmente en el servicio de listas de usuario cuando se intenta acceder a una lista por su ID y no se encuentra.
 */
public class UserListNotFoundException extends RuntimeException {
    public UserListNotFoundException(String message) {
        super(message);
    }
}
