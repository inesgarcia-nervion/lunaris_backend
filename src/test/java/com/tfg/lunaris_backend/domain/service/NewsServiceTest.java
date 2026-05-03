package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.NewsRepository;
import com.tfg.lunaris_backend.domain.model.News;
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
 * Test para la clase {@link NewsService}.
 */
@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsService svc;

    /**
     * Verifica que getAll delega en el repositorio y devuelve las noticias
     * ordenadas.
     */
    @Test
    void getAll_delegatesAndReturnsList() {
        News n = new News();
        n.setTitle("Título de prueba");
        when(newsRepository.findAllByOrderByIdDesc()).thenReturn(List.of(n));

        List<News> result = svc.getAll();

        assertEquals(1, result.size());
        assertEquals("Título de prueba", result.get(0).getTitle());
        verify(newsRepository).findAllByOrderByIdDesc();
    }

    /**
     * Verifica que getAll devuelve una lista vacía cuando no hay noticias.
     */
    @Test
    void getAll_emptyList() {
        when(newsRepository.findAllByOrderByIdDesc()).thenReturn(List.of());

        List<News> result = svc.getAll();

        assertTrue(result.isEmpty());
    }

    /**
     * Verifica que getById devuelve la noticia cuando existe.
     */
    @Test
    void getById_found_returnsNews() {
        News n = new News();
        n.setId(1L);
        n.setTitle("Noticia encontrada");
        when(newsRepository.findById(1L)).thenReturn(Optional.of(n));

        News result = svc.getById(1L);

        assertNotNull(result);
        assertEquals("Noticia encontrada", result.getTitle());
    }

    /**
     * Verifica que getById devuelve null cuando la noticia no existe.
     */
    @Test
    void getById_notFound_returnsNull() {
        when(newsRepository.findById(99L)).thenReturn(Optional.empty());

        News result = svc.getById(99L);

        assertNull(result);
    }

    /**
     * Verifica que create guarda y devuelve la noticia creada.
     */
    @Test
    void create_savesAndReturnsNews() {
        News n = new News();
        n.setTitle("Nueva noticia");
        n.setText("Texto breve");
        n.setBody("Cuerpo completo");
        n.setImage("imagen.jpg");
        n.setDate("2026-05-03T10:00:00.000");
        when(newsRepository.save(n)).thenReturn(n);

        News result = svc.create(n);

        assertSame(n, result);
        verify(newsRepository).save(n);
    }

    /**
     * Verifica que delete invoca deleteById en el repositorio.
     */
    @Test
    void delete_callsDeleteById() {
        svc.delete(5L);

        verify(newsRepository).deleteById(5L);
    }
}
