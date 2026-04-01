package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "posts", schema = "public")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Texto del post
    private String content;

    // Usuario que creó el post
    private String username;

    // Fecha/hora como cadena (puedes cambiar a Instant/LocalDateTime si prefieres)
    private String date;

    // Opcional: url de imagen adjunta
    private String imageUrl;
}
