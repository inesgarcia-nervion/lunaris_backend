package com.tfg.lunaris_backend.presentation.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

/**
 * Utilidad para trabajar con JWT (JSON Web Tokens).
 * 
 * Esta clase proporciona métodos para generar, validar y extraer información de tokens JWT.
 */
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:3600000}")
    private Long jwtExpirationMs;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = jwtSecret.getBytes();
        key = Keys.hmacShaKeyFor(bytes.length < 32 ? Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded() : bytes);
    }

    /**
     * Genera un token JWT para un usuario dado.
     * @param username nombre de usuario
     * @return token JWT
     */
    public String generateToken(String username) {
        return generateToken(username, "USER");
    }

    /**
     * Genera un token JWT para un usuario dado con un rol específico.
     * @param username nombre de usuario
     * @param role rol del usuario
     * @return token JWT
     */
    public String generateToken(String username, String role) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + jwtExpirationMs);
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", List.of(role))
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extrae el nombre de usuario de un token JWT.
     * @param token token JWT
     * @return nombre de usuario
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    /**
     * Valida un token JWT.
     * @param token token JWT
     * @return true si el token es válido, false en caso contrario
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
