package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa una publicación en la base de datos.
 * 
 * Contiene información sobre la publicación, incluyendo su contenido, el nombre de usuario del autor, 
 * la fecha de publicación y la URL de una imagen asociada.
 */
@Entity
@Table(name = "posts", schema = "public")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String username;

    private String date;

    private String imageUrl;
}
