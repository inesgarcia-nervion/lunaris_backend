package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.util.Collections;
import org.springframework.web.util.UriComponentsBuilder;

import com.tfg.lunaris_backend.domain.dto.OpenLibrarySearchResponseDto;

import lombok.extern.slf4j.Slf4j;

/**
 * Servicio que maneja la lógica de negocio relacionada con la búsqueda de libros en Open Library.
 * 
 * Proporciona métodos para buscar libros utilizando la API de Open Library.
 */
@Service
@Slf4j
public class OpenLibraryService {

    private static final String OPEN_LIBRARY_API_BASE_URL = "https://openlibrary.org/search.json";

    @Autowired
    private RestTemplate restTemplate;

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 1000; // 1s

    /**
     * Busca libros en Open Library utilizando un término de búsqueda general.
     * @param query término de búsqueda (puede ser título, autor, etc.)
     * @param limit número máximo de resultados a devolver (opcional, por defecto 10, máximo 1000) 
     * @param offset número de resultados a omitir para paginación (opcional, por defecto 0)
     * @return respuesta de búsqueda con la lista de libros encontrados y metadatos de la búsqueda
     */
    public OpenLibrarySearchResponseDto searchBooks(String query, Integer limit, Integer offset) {
        try {
            if (query == null || query.trim().isEmpty()) {
                throw new IllegalArgumentException("El término de búsqueda no puede estar vacío");
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
                    .queryParam("q", query)
                    .queryParam("limit", limit)
                    .queryParam("offset", offset)
                    .queryParam("fields",
                            "key,title,author_name,first_publish_year,cover_i,edition_count,ia,has_fulltext,description,ratings_average,subject")
                    .build()
                    .toUriString();

            log.info("Buscando en Open Library: {}", url);

            OpenLibrarySearchResponseDto response = fetchWithRetries(url);
            log.info("Se encontraron {} libros", response.getNumFound());
            return response;
        } catch (IllegalArgumentException e) {
            log.error("Error de validación: {}", e.getMessage());
            throw e;
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
     * Realiza la llamada a la API de Open Library con reintentos en caso de error o respuesta vacía.
     * @param url URL completa de la consulta a Open Library 
     * @return respuesta de búsqueda con la lista de libros encontrados y metadatos de la búsqueda, 
     * o respuesta vacía si no se obtienen resultados después de los reintentos
     */
    private OpenLibrarySearchResponseDto fetchWithRetries(String url) {
        long backoff = INITIAL_BACKOFF_MS;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                OpenLibrarySearchResponseDto resp = restTemplate.getForObject(url, OpenLibrarySearchResponseDto.class);
                if (resp != null && resp.getDocs() != null && !resp.getDocs().isEmpty()) {
                    return resp;
                }
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
        OpenLibrarySearchResponseDto empty = new OpenLibrarySearchResponseDto();
        empty.setNumFound(0);
        empty.setStart(0);
        empty.setDocs(Collections.emptyList());
        return empty;
    }

    /**
     * Busca libros por título en Open Library.
     * @param title título a buscar
     * @param limit número máximo de resultados a devolver (opcional, por defecto 10, máximo 1000) 
     * @param offset número de resultados a omitir para paginación (opcional, por defecto 0) 
     * @return respuesta de búsqueda con la lista de libros encontrados y metadatos de la búsqueda
     */
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

    /**
     * Busca libros por autor en Open Library. 
     * @param author autor a buscar
     * @param limit número máximo de resultados a devolver (opcional, por defecto 10, máximo 1000)
     * @param offset número de resultados a omitir para paginación (opcional, por defecto 0)
     * @return respuesta de búsqueda con la lista de libros encontrados y metadatos de la búsqueda
     */ 
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
