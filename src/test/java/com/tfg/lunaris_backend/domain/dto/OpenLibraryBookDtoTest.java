package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Test para la clase OpenLibraryBookDto.
 */
class OpenLibraryBookDtoTest {

    /**
     * Verifica que se obtiene el primer autor y la URL de la portada correctamente.
     */
    @Test
    void firstAuthorAndCoverUrl() {
        OpenLibraryBookDto d = new OpenLibraryBookDto();
        d.setAuthorNames(List.of("A1", "A2"));
        d.setCoverId(123);

        assertEquals("A1", d.getFirstAuthor());
        assertEquals("https://covers.openlibrary.org/b/id/123-M.jpg", d.getCoverUrl());

        d.setAuthorNames(List.of());
        assertNull(d.getFirstAuthor());

        d.setCoverId(null);
        assertNull(d.getCoverUrl());
    }

    /**
     * Verifica que se obtiene null cuando la lista de autores es null.
     */
    @Test
    void nullAuthorNames_returnsNull() {
        OpenLibraryBookDto d = new OpenLibraryBookDto();
        d.setAuthorNames(null);
        assertNull(d.getFirstAuthor());
    }
}
