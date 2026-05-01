package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa un usuario en la base de datos.
 * 
 * Contiene información sobre el usuario, incluyendo su nombre de usuario,
 * correo electrónico, contraseña y rol.
 */
@Entity
@Table(name = "users", schema = "public")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String email;
    private String password;
    private String role;
    @Column(columnDefinition = "text")
    private String avatarUrl;
}
