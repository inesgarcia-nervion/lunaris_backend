package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "books", schema = "public")
@Data // Lombok genera getters, setters, toString, equals, hashCode, etc.
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
}
