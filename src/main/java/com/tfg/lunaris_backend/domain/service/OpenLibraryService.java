package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;
import java.util.Collections;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;
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

            // Si la búsqueda general no devuelve resultados, intentar búsquedas por título y autor
            if (response != null && (response.getDocs() == null || response.getDocs().isEmpty())) {
                log.info("Búsqueda general en OpenLibrary sin resultados, intentando fallback por título y autor para: {}", query);
                try {
                    OpenLibrarySearchResponseDto titleResp = searchByTitle(query, limit, offset);
                    OpenLibrarySearchResponseDto authorResp = searchByAuthor(query, limit, offset);

                    Map<String, com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto> mergedMap = new LinkedHashMap<>();
                    if (titleResp != null && titleResp.getDocs() != null) {
                        for (com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto d : titleResp.getDocs()) {
                            if (d != null && d.getKey() != null) mergedMap.putIfAbsent(d.getKey(), d);
                        }
                    }
                    if (authorResp != null && authorResp.getDocs() != null) {
                        for (com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto d : authorResp.getDocs()) {
                            if (d != null && d.getKey() != null) mergedMap.putIfAbsent(d.getKey(), d);
                        }
                    }

                    OpenLibrarySearchResponseDto merged = new OpenLibrarySearchResponseDto();
                    List<com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto> docs = new ArrayList<>(mergedMap.values());
                    merged.setDocs(docs);
                    merged.setNumFound(docs.size());
                    merged.setStart(offset);
                    log.info("Fallback encontró {} libros para query={}", docs.size(), query);
                    return merged;
                } catch (Exception e) {
                    log.warn("Error en fallback de búsqueda por título/autor: {}", e.getMessage());
                    // Si falla el fallback, devolver la respuesta original (vacía)
                }
            }

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
     * Realiza la llamada a la API de Open Library con reintentos solo en caso de error de red.
     * No reintenta si Open Library devuelve una respuesta válida (aunque sea vacía).
     * @param url URL completa de la consulta a Open Library
     * @return respuesta de búsqueda con la lista de libros encontrados y metadatos de la búsqueda,
     * o respuesta vacía si no se obtienen resultados después de los reintentos
     */
    private OpenLibrarySearchResponseDto fetchWithRetries(String url) {
        long backoff = INITIAL_BACKOFF_MS;
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                OpenLibrarySearchResponseDto resp = restTemplate.getForObject(url, OpenLibrarySearchResponseDto.class);
                if (resp != null) {
                    if (resp.getDocs() == null) {
                        resp.setDocs(Collections.emptyList());
                    }
                    return resp;
                }
                log.warn("OpenLibrary returned null response (attempt {}), retrying... url={}", attempt, url);
            } catch (RestClientException e) {
                log.warn("RestTemplate call failed on attempt {}: {}", attempt, e.getMessage());
            }
            if (attempt < MAX_RETRIES) {
                try {
                    Thread.sleep(backoff);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
                backoff *= 2;
            }
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
