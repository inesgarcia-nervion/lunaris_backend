package com.tfg.lunaris_backend.presentation.exceptions;

public class UserListNotFoundException extends RuntimeException {
    public UserListNotFoundException(String message) {
        super(message);
    }
}
