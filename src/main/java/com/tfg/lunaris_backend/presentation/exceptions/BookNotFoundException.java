package com.tfg.lunaris_backend.presentation.exceptions;

/**
 * Excepción lanzada cuando no se encuentra un libro.
 * 
 * Esta excepción se utiliza para indicar que el libro solicitado no existe en la base de datos.
 * Se lanza principalmente en el servicio de libros cuando se intenta acceder a un libro por su ID y no se encuentra.
 */
public class BookNotFoundException extends RuntimeException {
    public BookNotFoundException(String message) {
        super(message);
    }

}
