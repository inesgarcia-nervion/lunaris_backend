package com.tfg.lunaris_backend.presentation.exceptions;

public class SagaNotFoundException extends RuntimeException {
    public SagaNotFoundException(String message) {
        super(message);
    }
}
