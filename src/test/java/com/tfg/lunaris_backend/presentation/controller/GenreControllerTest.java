package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.Genre;
import com.tfg.lunaris_backend.domain.service.GenreService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GenreControllerTest {
    @Test
    void delegates() {
        GenreService svc = mock(GenreService.class);
        GenreController c = new GenreController();
        ReflectionTestUtils.setField(c, "genreService", svc);

        Genre g = new Genre();
        g.setId(3L);
        when(svc.getAllGenres()).thenReturn(List.of(g));
        assertEquals(1, c.getAllGenres().size());

        when(svc.getGenreById(3L)).thenReturn(g);
        assertEquals(g, c.getGenreById(3L));

        when(svc.createGenre(g)).thenReturn(g);
        assertEquals(g, c.createGenre(g));

        when(svc.updateGenre(3L, g)).thenReturn(g);
        assertEquals(g, c.updateGenre(3L, g));

        c.deleteGenre(3L);
    }
}
