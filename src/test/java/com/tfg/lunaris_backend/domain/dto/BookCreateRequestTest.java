package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class BookCreateRequestTest {

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
