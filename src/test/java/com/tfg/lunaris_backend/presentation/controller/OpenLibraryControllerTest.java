package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.dto.OpenLibrarySearchResponseDto;
import com.tfg.lunaris_backend.domain.service.OpenLibraryService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OpenLibraryControllerTest {
    @Test
    void delegates() {
        OpenLibraryService svc = mock(OpenLibraryService.class);
        OpenLibraryController c = new OpenLibraryController();
        ReflectionTestUtils.setField(c, "openLibraryService", svc);

        OpenLibrarySearchResponseDto dto = new OpenLibrarySearchResponseDto();
        when(svc.searchBooks("q", null, null)).thenReturn(dto);
        assertEquals(dto, c.search("q", null, null));

        when(svc.searchByTitle("t", null, null)).thenReturn(dto);
        assertEquals(dto, c.searchByTitle("t", null, null));

        when(svc.searchByAuthor("a", null, null)).thenReturn(dto);
        assertEquals(dto, c.searchByAuthor("a", null, null));
    }
}
