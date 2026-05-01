package com.tfg.lunaris_backend.presentation.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Manejador global de excepciones.
 * 
 * Esta clase se encarga de capturar y manejar las excepciones lanzadas en los controladores,
 * devolviendo respuestas adecuadas al cliente.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Manejador de la excepción `BookNotFoundException`. Devuelve una respuesta con 
     * el mensaje de error y el estado HTTP 404 (Not Found).
     * @param ex excepción lanzada cuando no se encuentra un libro
     * @return respuesta con el mensaje de error y el estado HTTP 404 (Not Found)
     */
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<String> handleBookNotFoundException(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Manejador de la excepción `UserListNotFoundException`. Devuelve una respuesta con 
     * el mensaje de error y el estado HTTP 404 (Not Found).
     * @param ex excepción lanzada cuando no se encuentra una lista de usuarios
     * @return respuesta con el mensaje de error y el estado HTTP 404 (Not Found)
     */
    @ExceptionHandler(UserListNotFoundException.class)
    public ResponseEntity<String> handleUserListNotFoundException(UserListNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Manejador de la excepción `AuthorNotFoundException`. Devuelve una respuesta con el 
     * mensaje de error y el estado HTTP 404 (Not Found).
     * @param ex excepción lanzada cuando no se encuentra un autor
     * @return respuesta con el mensaje de error y el estado HTTP 404 (Not Found)
     */
    @ExceptionHandler(AuthorNotFoundException.class)
    public ResponseEntity<String> handleAuthorNotFoundException(AuthorNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Manejador de la excepción `GenreNotFoundException`. Devuelve una respuesta con el 
     * mensaje de error y el estado HTTP 404 (Not Found).
     * @param ex excepción lanzada cuando no se encuentra un género
     * @return respuesta con el mensaje de error y el estado HTTP 404 (Not Found)
     */
    @ExceptionHandler(GenreNotFoundException.class)
    public ResponseEntity<String> handleGenreNotFoundException(GenreNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Manejador de la excepción `ReviewNotFoundException`. Devuelve una respuesta con el 
     * mensaje de error y el estado HTTP 404 (Not Found).
     * @param ex excepción lanzada cuando no se encuentra una reseña
     * @return respuesta con el mensaje de error y el estado HTTP 404 (Not Found)
     */
    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<String> handleReviewNotFoundException(ReviewNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Manejador de la excepción `UserNotFoundException`. Devuelve una respuesta con el 
     * mensaje de error y el estado HTTP 404 (Not Found).
     * @param ex excepción lanzada cuando no se encuentra un usuario
     * @return respuesta con el mensaje de error y el estado HTTP 404 (Not Found)
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
    * Manejador de la excepción `SagaNotFoundException`. Devuelve una respuesta con el 
    * mensaje de error y el estado HTTP 404 (Not Found).
    * @param ex excepción lanzada cuando no se encuentra una saga
    * @return respuesta con el mensaje de error y el estado HTTP 404 (Not Found)
    */
    @ExceptionHandler(SagaNotFoundException.class)
    public ResponseEntity<String> handleSagaNotFoundException(SagaNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Manejador de la excepción `DuplicateBookException`. Devuelve una respuesta con
     * el mensaje de error y el estado HTTP 409 (Conflict).
     * @param ex excepción lanzada cuando ya existe un libro con el mismo título y autor
     * @return respuesta con el mensaje de error y el estado HTTP 409 (Conflict)
     */
    @ExceptionHandler(DuplicateBookException.class)
    public ResponseEntity<String> handleDuplicateBookException(DuplicateBookException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}