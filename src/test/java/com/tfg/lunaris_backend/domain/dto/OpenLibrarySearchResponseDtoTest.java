package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class OpenLibrarySearchResponseDtoTest {

    @Test
    void hasResultsWorks() {
        OpenLibrarySearchResponseDto r = new OpenLibrarySearchResponseDto();
        assertFalse(r.hasResults());

        r.setDocs(List.of(new OpenLibraryBookDto()));
        assertTrue(r.hasResults());
    }
}
