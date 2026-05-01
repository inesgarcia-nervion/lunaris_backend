package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.GenreRepository;
import com.tfg.lunaris_backend.domain.model.Genre;
import com.tfg.lunaris_backend.presentation.exceptions.GenreNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para la clase GenreService.
 */
@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository repo;

    @InjectMocks
    private GenreService svc;

    /**
     * Verifica los flujos básicos de la clase GenreService.
     */
    @Test
    void basicFlows() {
        Genre g = new Genre(); g.setName("F");
        when(repo.findAll()).thenReturn(List.of(g));
        assertFalse(svc.getAllGenres().isEmpty());

        when(repo.findById(1L)).thenReturn(Optional.of(g));
        assertSame(g, svc.getGenreById(1L));

        when(repo.findByNameIgnoreCase("F")).thenReturn(Optional.of(g));
        assertSame(g, svc.createGenre(g));

        when(repo.findById(2L)).thenReturn(Optional.of(g));
        when(repo.save(g)).thenReturn(g);
        Genre details = new Genre(); details.setName("G2");
        Genre up = svc.updateGenre(2L, details);
        assertEquals("G2", up.getName());

        svc.deleteGenre(3L);
        verify(repo).deleteById(3L);
    }

    /**
     * Verifica que se lanza una excepción cuando no se encuentra un género por ID.
     */
    @Test
    void getGenreByIdNotFoundThrows() {
        when(repo.findById(5L)).thenReturn(Optional.empty());
        assertThrows(GenreNotFoundException.class, () -> svc.getGenreById(5L));
    }

    /**
     * Verifica que se guarda un nuevo género cuando no existe previamente.
     */
    @Test
    void createGenre_notExisting_saves() {
        Genre g = new Genre(); g.setName("New");
        when(repo.findByNameIgnoreCase("New")).thenReturn(Optional.empty());
        when(repo.save(g)).thenReturn(g);
        assertSame(g, svc.createGenre(g));
        verify(repo).save(g);
    }

    /**
     * Verifica que se lanza una excepción cuando se intenta actualizar un género que no existe.
     */
    @Test
    void updateGenre_notFoundThrows() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(GenreNotFoundException.class, () -> svc.updateGenre(99L, new Genre()));
    }
}
