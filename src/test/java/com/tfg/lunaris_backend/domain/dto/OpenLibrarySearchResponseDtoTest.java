package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Test para la clase OpenLibrarySearchResponseDto.
 */
class OpenLibrarySearchResponseDtoTest {

    /**
     * Verifica que el método hasResults funciona correctamente.
     */
    @Test
    void hasResultsWorks() {
        OpenLibrarySearchResponseDto r = new OpenLibrarySearchResponseDto();
        assertFalse(r.hasResults());

        r.setDocs(List.of(new OpenLibraryBookDto()));
        assertTrue(r.hasResults());
    }
}
