package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.AuthorRepository;
import com.tfg.lunaris_backend.domain.model.Author;
import com.tfg.lunaris_backend.presentation.exceptions.AuthorNotFoundException;
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
 * Test para la clase AuthorService.
 */
@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository repo;

    @InjectMocks
    private AuthorService svc;

    /**
     * Verifica que se delega correctamente la obtención de todos los autores al repositorio.
     */
    @Test
    void getAllAuthorsDelegates() {
        when(repo.findAll()).thenReturn(List.of(new Author()));
        assertFalse(svc.getAllAuthors().isEmpty());
        verify(repo).findAll();
    }

    /**
     * Verifica que se puede obtener un autor por su ID cuando existe.
     */
    @Test
    void getAuthorByIdFound() {
        Author a = new Author(); a.setName("X");
        when(repo.findById(1L)).thenReturn(Optional.of(a));
        assertEquals("X", svc.getAuthorById(1L).getName());
    }

    /**
     * Verifica que se lanza una excepción cuando se intenta obtener un autor por un ID que no existe.
     */
    @Test
    void getAuthorByIdNotFoundThrows() {
        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(AuthorNotFoundException.class, () -> svc.getAuthorById(2L));
    }

    /**
     * Verifica que se pueden crear, actualizar y eliminar autores correctamente.
     */
    @Test
    void createAndUpdateAndDelete() {
        Author a = new Author(); a.setName("A");
        when(repo.save(a)).thenReturn(a);
        assertSame(a, svc.createAuthor(a));

        Author details = new Author(); details.setName("B"); details.setBooks("bks");
        when(repo.findById(3L)).thenReturn(Optional.of(a));
        when(repo.save(a)).thenReturn(a);
        Author updated = svc.updateAuthor(3L, details);
        assertEquals("B", updated.getName());

        svc.deleteAuthor(4L);
        verify(repo).deleteById(4L);
    }

    /**
     * Verifica que se lanza una excepción cuando se intenta actualizar un autor que no existe.
     */
    @Test
    void updateAuthor_notFoundThrows() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(AuthorNotFoundException.class, () -> svc.updateAuthor(99L, new Author()));
    }
}
