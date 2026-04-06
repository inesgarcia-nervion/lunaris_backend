package com.tfg.lunaris_backend.presentation.exceptions;

/**
 * Excepción lanzada cuando no se encuentra una reseña.
 * 
 * Esta excepción se utiliza para indicar que la saga solicitada no existe en la base de datos.
 * Se lanza principalmente en el servicio de sagas cuando se intenta acceder a una saga por su ID y no se encuentra.
 */
public class SagaNotFoundException extends RuntimeException {
    public SagaNotFoundException(String message) {
        super(message);
    }
}
