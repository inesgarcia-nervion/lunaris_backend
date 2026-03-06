package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String email;
}
