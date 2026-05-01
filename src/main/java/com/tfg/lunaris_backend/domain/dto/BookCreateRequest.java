package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la creación de un nuevo libro.
 *
 * Contiene los campos necesarios para crear un nuevo libro en el sistema,
 * incluyendo título,
 * imagen de portada, descripción, autor, año de lanzamiento, puntuación,
 * fuente, ID del
 * usuario que lo crea y una lista de IDs de géneros asociados.
 */
@Data
@NoArgsConstructor
public class BookCreateRequest {
    private String title;
    private String coverImage;
    private String description;
    private String author;
    private String apiId;
    private Integer releaseYear;
    private Double score;
    private List<Long> genreIds;
}
