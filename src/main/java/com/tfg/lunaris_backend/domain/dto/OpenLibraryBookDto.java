package com.tfg.lunaris_backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa un libro tal y como viene en la respuesta de OpenLibrary.
 * 
 * Contiene los campos relevantes para un libro, incluyendo título, autor, año 
 * de publicación, imagen de portada, descripción, puntuación y otros detalles 
 * que pueden ser útiles para mostrar información al usuario.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryBookDto {

    @JsonProperty("key")
    private String key;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author_name")
    private List<String> authorNames;

    @JsonProperty("first_publish_year")
    private Integer firstPublishYear;

    @JsonProperty("cover_i")
    private Integer coverId;

    @JsonProperty("edition_count")
    private Integer editionCount;

    @JsonProperty("ia")
    private List<String> internetArchiveIds;

    @JsonProperty("has_fulltext")
    private Boolean hasFulltext;

    @JsonProperty("description")
    private String description;

    @JsonProperty("ratings_average")
    private Double ratingsAverage;

    @JsonProperty("subject")
    private List<String> subject;

    public String getFirstAuthor() {
        return (authorNames != null && !authorNames.isEmpty()) ? authorNames.get(0) : null;
    }

    public String getCoverUrl() {
        if (coverId != null) {
            return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
        }
        return null;
    }
}
