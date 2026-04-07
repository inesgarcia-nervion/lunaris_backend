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

@ExtendWith(MockitoExtension.class)
class OpenLibraryServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private OpenLibraryService svc;

    @Test
    void searchBooks_validationAndRestFailure() {
        assertThrows(IllegalArgumentException.class, () -> svc.searchBooks("  ", 10, 0));

        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenThrow(new RestClientException("fail"));

        OpenLibrarySearchResponseDto r = svc.searchBooks("query", 5, 0);
        assertNotNull(r);
        assertEquals(0, r.getNumFound());
    }

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

    @Test
    void searchByTitle_emptyTitle_throwsRuntime() {
        assertThrows(RuntimeException.class, () -> svc.searchByTitle("  ", null, null));
    }

    @Test
    void searchByAuthor_emptyAuthor_throwsRuntime() {
        assertThrows(RuntimeException.class, () -> svc.searchByAuthor("", null, null));
    }

    @Test
    void fetchWithRetries_interruptedMidRetry() {
        // Return an empty-docs response so fetchWithRetries wants to retry,
        // but setting the interrupt flag makes Thread.sleep throw immediately.
        OpenLibrarySearchResponseDto emptyDocs = new OpenLibrarySearchResponseDto();
        emptyDocs.setNumFound(0);
        emptyDocs.setDocs(java.util.List.of());
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenReturn(emptyDocs);

        Thread.currentThread().interrupt(); // causes Thread.sleep to throw IE instantly
        try {
            OpenLibrarySearchResponseDto result = svc.searchBooks("query", 5, 0);
            assertNotNull(result);
        } finally {
            Thread.interrupted(); // clear interrupt flag regardless
        }
    }

    @Test
    void searchBooks_nullOffsetDefaulted() {
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenThrow(new RestClientException("fail"));
        OpenLibrarySearchResponseDto r = svc.searchBooks("q", null, null);
        assertNotNull(r);
        assertEquals(0, r.getNumFound());
    }

    @Test
    void searchBooks_limitOver1000_clamped() {
        // limit > 1000 → clamped to 1000 (covers line with limit = 1000)
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenThrow(new RestClientException("fail"));
        OpenLibrarySearchResponseDto r = svc.searchBooks("q", 2000, 0);
        assertNotNull(r);
        assertEquals(0, r.getNumFound());
    }

    @Test
    void searchBooks_unexpectedException_returnsEmpty() {
        // fetchWithRetries propagates a non-RestClientException RuntimeException
        // → caught by catch (Exception e) in searchBooks
        when(restTemplate.getForObject(anyString(), eq(OpenLibrarySearchResponseDto.class)))
                .thenThrow(new RuntimeException("unexpected error"));
        OpenLibrarySearchResponseDto r = svc.searchBooks("q", 5, 0);
        assertNotNull(r);
        assertEquals(0, r.getNumFound());
    }
}
