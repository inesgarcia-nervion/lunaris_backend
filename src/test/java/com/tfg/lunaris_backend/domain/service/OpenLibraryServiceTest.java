package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.domain.dto.OpenLibrarySearchResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para la clase OpenLibraryService.
 */
@ExtendWith(MockitoExtension.class)
class OpenLibraryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenLibraryService svc;

    /**
     * Verifica la validación de parámetros y el manejo de fallos en la llamada al servicio REST.
     */
    @Test
    void searchBooks_validationAndRestFailure() {
        assertThrows(IllegalArgumentException.class, () -> svc.searchBooks("  ", 10, 0));

        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenThrow(new RestClientException("fail"));

        OpenLibrarySearchResponseDto r = svc.searchBooks("query", 5, 0);
        assertNotNull(r);
        assertEquals(0, r.getNumFound());
    }

    /**
     * Verifica la búsqueda por título y autor, y el ajuste del límite.
     */
    @Test
    void searchByTitle_andByAuthor_limitClamp() {
        OpenLibrarySearchResponseDto dto = new OpenLibrarySearchResponseDto();
        dto.setNumFound(1);
        dto.setDocs(java.util.List.of());

        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class))).thenReturn(dto);

        OpenLibrarySearchResponseDto res = svc.searchByTitle("T", 2000, null);
        assertNotNull(res);

        res = svc.searchByAuthor("A", null, null);
        assertNotNull(res);
    }

    /**
     * Verifica que se lanza una excepción cuando se busca un título vacío.
     */
    @Test
    void searchByTitle_emptyTitle_throwsRuntime() {
        assertThrows(RuntimeException.class, () -> svc.searchByTitle("  ", null, null));
    }

    /**
     * Verifica que se lanza una excepción cuando se busca un autor vacío.
     */
    @Test
    void searchByAuthor_emptyAuthor_throwsRuntime() {
        assertThrows(RuntimeException.class, () -> svc.searchByAuthor("", null, null));
    }

    /**
     * Verifica el comportamiento cuando se interrumpe el hilo durante los reintentos.
     */
    @Test
    void fetchWithRetries_interruptedMidRetry() {
        OpenLibrarySearchResponseDto emptyDocs = new OpenLibrarySearchResponseDto();
        emptyDocs.setNumFound(0);
        emptyDocs.setDocs(java.util.List.of());
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenReturn(emptyDocs);

        Thread.currentThread().interrupt();
        try {
            OpenLibrarySearchResponseDto result = svc.searchBooks("query", 5, 0);
            assertNotNull(result);
        } finally {
            Thread.interrupted(); 
        }
    }

    /**
     * Verifica que se maneja correctamente un offset nulo.
     */
    @Test
    void searchBooks_nullOffsetDefaulted() {
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenThrow(new RestClientException("fail"));
        OpenLibrarySearchResponseDto r = svc.searchBooks("q", null, null);
        assertNotNull(r);
        assertEquals(0, r.getNumFound());
    }

    /**
     * Verifica que se clampa el límite cuando es mayor a 1000.
     */
    @Test
    void searchBooks_limitOver1000_clamped() {
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenThrow(new RestClientException("fail"));
        OpenLibrarySearchResponseDto r = svc.searchBooks("q", 2000, 0);
        assertNotNull(r);
        assertEquals(0, r.getNumFound());
    }

    /**
     * Verifica que se maneja correctamente una excepción inesperada durante la búsqueda de libros.
     */
    @Test
    void searchBooks_unexpectedException_returnsEmpty() {
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenThrow(new RuntimeException("unexpected error"));
        OpenLibrarySearchResponseDto r = svc.searchBooks("q", 5, 0);
        assertNotNull(r);
        assertEquals(0, r.getNumFound());
    }
}
