package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa una reseña en la base de datos.
 * 
 * Contiene información sobre la reseña, incluyendo el comentario, la puntuación, la fecha, 
 * el identificador de la API del libro, el título del libro, la URL de la portada y el nombre de usuario del autor.
 */
@Entity
@Table(name = "reviews", schema = "public")
@Data 
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private Double rating; 
    private String date;
    private String bookApiId;
    private String bookTitle;
    private String coverUrl;
    private String username;
}