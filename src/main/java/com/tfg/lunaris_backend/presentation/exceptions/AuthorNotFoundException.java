package com.tfg.lunaris_backend.presentation.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un autor.
 * 
 * Esta excepción se utiliza para indicar que el autor solicitado no existe en la base de datos.
 * Se lanza principalmente en el servicio de autores cuando se intenta acceder a un autor por su ID y no se encuentra.
 */
public class AuthorNotFoundException extends RuntimeException {
    public AuthorNotFoundException(String message) {
        super(message);
    }
}
