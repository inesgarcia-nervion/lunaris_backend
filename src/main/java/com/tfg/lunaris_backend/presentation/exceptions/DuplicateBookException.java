package com.tfg.lunaris_backend.presentation.exceptions;

/**
 * Excepción lanzada cuando se intenta crear un libro que ya existe.
 *
 * Se considera duplicado si ya existe un libro con el mismo título y autor
 * (sin distinguir mayúsculas/minúsculas).
 */
public class DuplicateBookException extends RuntimeException {
    public DuplicateBookException(String message) {
        super(message);
    }
}
