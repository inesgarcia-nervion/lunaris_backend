package com.tfg.lunaris_backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para representar una saga de libros obtenida a través del web scraping.
 * 
 * Contiene el nombre de la saga y una lista de libros que pertenecen a esa saga, 
 * donde cada libro incluye detalles como título, autor, número de orden en la saga, 
 * número de páginas, año de publicación y URL en The StoryGraph.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagaScrapedDto {

    private String sagaName;
    private List<SagaBookEntry> books;

    /**
     * DTO para representar un libro dentro de una saga obtenida a través del web scraping.
     * 
     * Contiene detalles como título, autor, número de orden en la saga, número de páginas, 
     * año de publicación y URL en The StoryGraph.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SagaBookEntry {
        private String title;
        private String author;
        private String orderNumber;
        private Integer pages;
        private Integer year;
        private String storygraphUrl;
    }
}
