package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;

@Data
public class NewPasswordRequest {
    private String token;
    private String newPassword;
}
