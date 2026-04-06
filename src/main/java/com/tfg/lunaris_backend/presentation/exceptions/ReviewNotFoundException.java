package com.tfg.lunaris_backend.presentation.exceptions;

/**
 * Excepción lanzada cuando no se encuentra una reseña.
 * 
 * Esta excepción se utiliza para indicar que la reseña solicitada no existe en la base de datos.
 * Se lanza principalmente en el servicio de reseñas cuando se intenta acceder a una reseña por su ID y no se encuentra.
 */
public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String message) {
        super(message);
    }
}
