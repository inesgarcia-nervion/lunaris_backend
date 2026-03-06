package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.dto.AuthRequest;
import com.tfg.lunaris_backend.domain.dto.AuthResponse;
import com.tfg.lunaris_backend.domain.dto.NewPasswordRequest;
import com.tfg.lunaris_backend.domain.dto.PasswordResetRequest;
import com.tfg.lunaris_backend.domain.service.PasswordResetService;
import com.tfg.lunaris_backend.presentation.security.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            String token = jwtUtils.generateToken(request.getUsername());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequest request) {
        String result = passwordResetService.requestPasswordReset(request.getEmail());

        return switch (result) {
            case "SUCCESS" -> ResponseEntity
                    .ok(Map.of("message", "Se ha enviado un correo con instrucciones para restablecer tu contraseña."));
            case "EMAIL_NOT_FOUND" -> ResponseEntity.status(404)
                    .body(Map.of("error", "No existe ninguna cuenta con ese correo electrónico."));
            default ->
                ResponseEntity.status(500).body(Map.of("error", "Error al enviar el correo. Inténtalo de nuevo."));
        };
    }

    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        if (valid) {
            return ResponseEntity.ok(Map.of("valid", true));
        }
        return ResponseEntity.status(400)
                .body(Map.of("valid", false, "error", "El enlace ha expirado o no es válido."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody NewPasswordRequest request) {
        if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
            return ResponseEntity.status(400).body(Map.of("error", "La contraseña debe tener al menos 6 caracteres."));
        }

        boolean result = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        if (result) {
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada correctamente."));
        }
        return ResponseEntity.status(400).body(Map.of("error", "El enlace ha expirado o no es válido."));
    }
}
