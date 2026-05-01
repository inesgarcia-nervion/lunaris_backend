package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.Author;
import com.tfg.lunaris_backend.domain.service.AuthorService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test para {@link AuthorController}.
 */
class AuthorControllerTest {

    /**
     * Verifica que los métodos del controlador delegan correctamente en el servicio.
     */
    @Test
    void delegates() {
        AuthorService svc = mock(AuthorService.class);
        AuthorController c = new AuthorController();
        ReflectionTestUtils.setField(c, "authorService", svc);

        Author a = new Author();
        a.setId(2L);
        when(svc.getAllAuthors()).thenReturn(List.of(a));
        assertEquals(1, c.getAllAuthors().size());

        when(svc.getAuthorById(2L)).thenReturn(a);
        assertEquals(a, c.getAuthorById(2L));

        when(svc.createAuthor(a)).thenReturn(a);
        assertEquals(a, c.createAuthor(a));

        when(svc.updateAuthor(2L, a)).thenReturn(a);
        assertEquals(a, c.updateAuthor(2L, a));

        c.deleteAuthor(2L);
    }
}
