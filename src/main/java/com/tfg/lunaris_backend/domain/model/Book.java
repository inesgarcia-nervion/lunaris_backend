package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa un libro en la base de datos.
 * 
 * Contiene información sobre el libro, incluyendo título, imagen de portada, descripción, autor, 
 * identificador de la API, año de publicación, puntuación y géneros asociados.
 */
@Entity
@Table(name = "books", schema = "public")
@Data 
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String coverImage;
    private String description;
    private String author;
    private String apiId;
    private Integer releaseYear;
    private Double score;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "book_genres", schema = "public", joinColumns = @JoinColumn(name = "book_id"), inverseJoinColumns = @JoinColumn(name = "genre_id"))
    private List<Genre> genres = new ArrayList<>();
}
