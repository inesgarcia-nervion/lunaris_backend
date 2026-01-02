package com.tfg.lunaris_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ BookNotFoundException.class, UserListNotFoundException.class, AuthorNotFoundException.class,
            GenreNotFoundException.class, ReviewNotFoundException.class, UserNotFoundException.class,
            SagaNotFoundException.class })
    public ResponseEntity<String> handleBookNotFoundException(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }
}
