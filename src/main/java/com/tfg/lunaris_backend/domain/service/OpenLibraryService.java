package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.util.Collections;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.lunaris_backend.domain.dto.OpenLibrarySearchResponseDto;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OpenLibraryService {

    private static final String OPEN_LIBRARY_API_BASE_URL = "https://openlibrary.org/search.json";

    @Autowired
    private RestTemplate restTemplate;

    // Simple retry configuration for flaky external API calls
    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000; // 1s

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
            if (limit > 1000) {
                limit = 1000; // Open Library soporta hasta 1000 resultados por petición
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
                            "key,title,author_name,first_publish_year,cover_i,edition_count,ia,has_fulltext,description,ratings_average,subject")
                    .build()
                    .toUriString();

            log.info("Buscando en Open Library: {}", url);

            OpenLibrarySearchResponseDto response = fetchWithRetries(url);
            if (response != null) {
                log.info("Se encontraron {} libros", response.getNumFound());
                return response;
            }
            // Si la respuesta es null, devolver un DTO vacío en lugar de propagar null
            log.warn("Respuesta nula de OpenLibrary para la consulta: {}", query);
            OpenLibrarySearchResponseDto empty = new OpenLibrarySearchResponseDto();
            empty.setNumFound(0);
            empty.setStart(offset);
            empty.setDocs(Collections.emptyList());
            return empty;
        } catch (IllegalArgumentException e) {
            log.error("Error de validación: {}", e.getMessage());
            throw e;
        } catch (RestClientException e) {
            // Timeout or HTTP error when calling OpenLibrary: log and return empty result
            log.warn("Error en llamada a OpenLibrary (RestClientException): {}", e.getMessage());
            OpenLibrarySearchResponseDto empty = new OpenLibrarySearchResponseDto();
            empty.setNumFound(0);
            empty.setStart(offset);
            empty.setDocs(Collections.emptyList());
            return empty;
        } catch (Exception e) {
            log.error("Error inesperado al buscar en Open Library: {}", e.getMessage(), e);
            OpenLibrarySearchResponseDto empty = new OpenLibrarySearchResponseDto();
            empty.setNumFound(0);
            empty.setStart(offset);
            empty.setDocs(Collections.emptyList());
            return empty;
        }
    }

    /**
     * Fetch URL with simple retry and exponential backoff. Returns empty DTO on
     * persistent failures.
     */
    private OpenLibrarySearchResponseDto fetchWithRetries(String url) {
        long backoff = INITIAL_BACKOFF_MS;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                OpenLibrarySearchResponseDto resp = restTemplate.getForObject(url, OpenLibrarySearchResponseDto.class);
                if (resp != null && resp.getDocs() != null && !resp.getDocs().isEmpty()) {
                    return resp;
                }
                // If empty but HTTP OK, treat as possible transient and retry
                log.warn("OpenLibrary returned empty result (attempt {}), retrying... url={}", attempt, url);
            } catch (RestClientException e) {
                log.warn("RestTemplate call failed on attempt {}: {}", attempt, e.getMessage());
            }
            try {
                Thread.sleep(backoff);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                break;
            }
            backoff *= 2;
        }
        // give up, return empty DTO
        OpenLibrarySearchResponseDto empty = new OpenLibrarySearchResponseDto();
        empty.setNumFound(0);
        empty.setStart(0);
        empty.setDocs(Collections.emptyList());
        return empty;
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
            if (limit > 1000) {
                limit = 1000;
            }
            if (offset == null) {
                offset = 0;
            }

            String url = UriComponentsBuilder.fromUri(java.net.URI.create(OPEN_LIBRARY_API_BASE_URL))
                    .queryParam("title", title)
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                    .queryParam("fields",
                            "key,title,author_name,first_publish_year,cover_i,edition_count,ia,has_fulltext,description,ratings_average,subject")
                    .build()
                    .toUriString();

            log.info("Buscando por título en Open Library: {}", title);
            return fetchWithRetries(url);
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
            if (limit > 1000) {
                limit = 1000;
            }
            if (offset == null) {
                offset = 0;
            }

            String url = UriComponentsBuilder.fromUri(java.net.URI.create(OPEN_LIBRARY_API_BASE_URL))
                    .queryParam("author", author)
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                    .queryParam("fields",
                            "key,title,author_name,first_publish_year,cover_i,edition_count,ia,has_fulltext,description,ratings_average,subject")
                    .build()
                    .toUriString();

            log.info("Buscando por autor en Open Library: {}", author);
            return fetchWithRetries(url);
        } catch (Exception e) {
            log.error("Error al buscar por autor: {}", e.getMessage(), e);
            throw new RuntimeException("Error al buscar por autor: " + e.getMessage(), e);
        }
    }
}
