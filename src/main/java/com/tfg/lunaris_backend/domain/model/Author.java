package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


/**
 * Entidad que representa un autor en la base de datos.
 * 
 * Contiene información sobre el autor, incluyendo su nombre y los libros que ha escrito.
 */
@Entity
@Table(name = "authors", schema = "public")
@Data 
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String books;
}
