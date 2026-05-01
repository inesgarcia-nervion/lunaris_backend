package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

/**
 * Test para la clase BookCreateRequest.
 */
class BookCreateRequestTest {

    /**
     * Verifica que se pueden establecer y obtener las propiedades de la clase.
     */
    @Test
    void properties() {
        BookCreateRequest r = new BookCreateRequest();
        r.setTitle("T");
        r.setAuthor("A");
        r.setGenreIds(List.of(1L,2L));

        assertEquals("T", r.getTitle());
        assertEquals("A", r.getAuthor());
        assertEquals(2, r.getGenreIds().size());
    }
}
