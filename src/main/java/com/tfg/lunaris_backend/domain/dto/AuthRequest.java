package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String username;
    private String password;
}
