package com.tfg.lunaris_backend.presentation.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un género.
 * 
 * Esta excepción se utiliza para indicar que el género solicitado no existe en la base de datos.
 * Se lanza principalmente en el servicio de géneros cuando se intenta acceder a un género por su ID y no se encuentra.
 */
public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(String message) {
        super(message);
    }
}
