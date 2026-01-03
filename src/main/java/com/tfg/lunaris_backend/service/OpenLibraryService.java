package com.tfg.lunaris_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.lunaris_backend.dto.OpenLibrarySearchResponseDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpenLibraryService {

    private static final String OPEN_LIBRARY_API_BASE_URL = "https://openlibrary.org/search.json";

    @Autowired
    private RestTemplate restTemplate;

    // Busca libros en Open Library por título, autor o texto libre
    public OpenLibrarySearchResponseDto searchBooks(String query, Integer limit, Integer offset) {
        try {
            if (query == null || query.trim().isEmpty()) {
                throw new IllegalArgumentException("El término de búsqueda no puede estar vacío");
            }

            // Establecer valores por defecto
            if (limit == null) {
                limit = 10;
            }
            if (limit > 100) {
                limit = 100; // Open Library tiene un límite máximo
            }
            if (offset == null) {
                offset = 0;
            }

            // Construir la URL con parámetros
            String url = UriComponentsBuilder.fromUri(java.net.URI.create(OPEN_LIBRARY_API_BASE_URL))
                    .queryParam("q", query)
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                    .queryParam("fields",
                            "key,title,author_name,first_publish_year,cover_i,edition_count,ia,has_fulltext,description,ratings_average")
                    .build()
                    .toUriString();

            log.info("Buscando en Open Library: {}", url);

            OpenLibrarySearchResponseDto response = restTemplate.getForObject(url, OpenLibrarySearchResponseDto.class);

            if (response != null) {
                log.info("Se encontraron {} libros", response.getNumFound());
            }

            return response;
        } catch (IllegalArgumentException e) {
            log.error("Error de validación: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error al buscar en Open Library: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar en Open Library: " + e.getMessage(), e);
        }
    }

    // Busca libros por título específicamente
    public OpenLibrarySearchResponseDto searchByTitle(String title, Integer limit, Integer offset) {
        try {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalArgumentException("El título no puede estar vacío");
            }

            if (limit == null) {
                limit = 10;
            }
            if (limit > 100) {
                limit = 100;
            }
            if (offset == null) {
                offset = 0;
            }

            String url = UriComponentsBuilder.fromUri(java.net.URI.create(OPEN_LIBRARY_API_BASE_URL))
                    .queryParam("title", title)
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                    .queryParam("fields",
                            "key,title,author_name,first_publish_year,cover_i,edition_count,ia,has_fulltext,description,ratings_average")
                    .build()
                    .toUriString();

            log.info("Buscando por título en Open Library: {}", title);

            return restTemplate.getForObject(url, OpenLibrarySearchResponseDto.class);
        } catch (Exception e) {
            log.error("Error al buscar por título: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar por título: " + e.getMessage(), e);
        }
    }

    // Busca libros por autor específicamente
    public OpenLibrarySearchResponseDto searchByAuthor(String author, Integer limit, Integer offset) {
        try {
            if (author == null || author.trim().isEmpty()) {
                throw new IllegalArgumentException("El autor no puede estar vacío");
            }

            if (limit == null) {
                limit = 10;
            }
            if (limit > 100) {
                limit = 100;
            }
            if (offset == null) {
                offset = 0;
            }

            String url = UriComponentsBuilder.fromUri(java.net.URI.create(OPEN_LIBRARY_API_BASE_URL))
                    .queryParam("author", author)
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                    .queryParam("fields",
                            "key,title,author_name,first_publish_year,cover_i,edition_count,ia,has_fulltext,description,ratings_average")
                    .build()
                    .toUriString();

            log.info("Buscando por autor en Open Library: {}", author);

            return restTemplate.getForObject(url, OpenLibrarySearchResponseDto.class);
        } catch (Exception e) {
            log.error("Error al buscar por autor: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar por autor: " + e.getMessage(), e);
        }
    }
}
