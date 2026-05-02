package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad que representa una noticia en la base de datos.
 */
@Entity
@Table(name = "news", schema = "public")
@Data
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Column(columnDefinition = "TEXT")
    private String image;

    @Column(nullable = false)
    private String date;
}
