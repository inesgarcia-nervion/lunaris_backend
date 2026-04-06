package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.dto.AuthRequest;
import com.tfg.lunaris_backend.domain.dto.AuthResponse;
import com.tfg.lunaris_backend.domain.dto.NewPasswordRequest;
import com.tfg.lunaris_backend.domain.dto.PasswordResetRequest;
import com.tfg.lunaris_backend.domain.service.PasswordResetService;
import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.presentation.security.JwtUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador que maneja las operaciones de autenticación y restablecimiento de contraseña.
 * 
 * Proporciona endpoints para iniciar sesión, solicitar restablecimiento de contraseña, 
 * validar tokens de restablecimiento y actualizar contraseñas.
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetService passwordResetService;

    /**
     * Endpoint para iniciar sesión. Recibe un objeto `AuthRequest` con el nombre de usuario y la contraseña,
     * y devuelve un token JWT si las credenciales son válidas.
     * @param request objeto con el nombre de usuario y la contraseña
     * @return ResponseEntity con el token JWT o un mensaje de error
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            String role = userRepository.findByUsername(request.getUsername())
                    .map(u -> u.getRole() != null ? u.getRole() : "USER")
                    .orElse("USER");

            String token = jwtUtils.generateToken(request.getUsername(), role);
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }

    /**
     * Endpoint para solicitar el restablecimiento de contraseña. Recibe un objeto `PasswordResetRequest` con el correo electrónico,
     * y envía un correo con instrucciones para restablecer la contraseña si el correo existe.
     * @param request objeto con el correo electrónico
     * @return ResponseEntity con un mensaje de éxito o error
     */
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

    /**
     * Endpoint para validar un token de restablecimiento de contraseña. Recibe un token como parámetro,
     * y devuelve si el token es válido o no.
     * @param token token de restablecimiento de contraseña
     * @return ResponseEntity con el estado de validez del token
     */
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        boolean valid = passwordResetService.validateToken(token);
        if (valid) {
            return ResponseEntity.ok(Map.of("valid", true));
        }
        return ResponseEntity.status(400)
                .body(Map.of("valid", false, "error", "El enlace ha expirado o no es válido."));
    }

    /**
     * Endpoint para restablecer la contraseña. Recibe un objeto `NewPasswordRequest` con el token y la nueva contraseña,
     * y actualiza la contraseña si el token es válido.
     * @param request objeto con el token y la nueva contraseña
     * @return ResponseEntity con un mensaje de éxito o error
     */
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
