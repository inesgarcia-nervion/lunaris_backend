package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.dto.NewsRequestDto;
import com.tfg.lunaris_backend.domain.model.News;
import com.tfg.lunaris_backend.domain.service.NewsService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para {@link NewsController}.
 */
class NewsControllerTest {

    /**
     * Construye un {@link NewsController} con el servicio inyectado vía reflexión.
     *
     * @param svc Servicio de noticias a inyectar.
     * @return Instancia de {@link NewsController} configurada.
     */
    private NewsController buildController(NewsService svc) {
        NewsController c = new NewsController();
        ReflectionTestUtils.setField(c, "newsService", svc);
        return c;
    }

    /**
     * Verifica que getAll devuelve todas las noticias del servicio.
     */
    @Test
    void getAll_returnsList() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        News n = new News();
        n.setId(1L);
        n.setTitle("Noticia 1");
        when(svc.getAll()).thenReturn(List.of(n));

        List<News> result = c.getAll();

        assertEquals(1, result.size());
        assertEquals("Noticia 1", result.get(0).getTitle());
    }

    /**
     * Verifica que getById devuelve la noticia cuando existe.
     */
    @Test
    void getById_found_returnsNews() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        News n = new News();
        n.setId(2L);
        n.setTitle("Noticia existente");
        when(svc.getById(2L)).thenReturn(n);

        News result = c.getById(2L);

        assertNotNull(result);
        assertEquals("Noticia existente", result.getTitle());
    }

    /**
     * Verifica que getById lanza NOT_FOUND cuando la noticia no existe.
     */
    @Test
    void getById_notFound_throwsNotFound() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        when(svc.getById(99L)).thenReturn(null);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.getById(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    /**
     * Verifica que create funciona correctamente cuando el usuario es
     * administrador.
     */
    @Test
    void create_asAdmin_createsNews() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        NewsRequestDto dto = new NewsRequestDto();
        dto.setTitle("Título");
        dto.setText("Texto breve");
        dto.setBody("Cuerpo completo");
        dto.setImage("imagen.jpg");

        News saved = new News();
        saved.setId(1L);
        saved.setTitle("Título");
        when(svc.create(any())).thenReturn(saved);

        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();

        News result = c.create(dto, auth);

        assertNotNull(result);
        assertEquals("Título", result.getTitle());
        verify(svc).create(any());
    }

    /**
     * Verifica que create lanza UNAUTHORIZED cuando el usuario no está autenticado.
     */
    @Test
    void create_noAuth_throwsUnauthorized() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);
        NewsRequestDto dto = new NewsRequestDto();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.create(dto, null));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    /**
     * Verifica que create lanza FORBIDDEN cuando el usuario no es administrador.
     */
    @Test
    void create_notAdmin_throwsForbidden() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        NewsRequestDto dto = new NewsRequestDto();
        Authentication auth = mock(Authentication.class);
        doReturn(Collections.emptyList()).when(auth).getAuthorities();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.create(dto, auth));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    /**
     * Verifica que delete funciona correctamente cuando el usuario es administrador
     * y la noticia existe.
     */
    @Test
    void delete_asAdmin_deletesNews() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        News n = new News();
        n.setId(3L);
        when(svc.getById(3L)).thenReturn(n);

        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();

        c.delete(3L, auth);

        verify(svc).delete(3L);
    }

    /**
     * Verifica que delete lanza NOT_FOUND cuando la noticia no existe.
     */
    @Test
    void delete_notFound_throwsNotFound() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        when(svc.getById(77L)).thenReturn(null);

        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.delete(77L, auth));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    /**
     * Verifica que delete lanza UNAUTHORIZED cuando el usuario no está autenticado.
     */
    @Test
    void delete_noAuth_throwsUnauthorized() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.delete(1L, null));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    /**
     * Verifica que delete lanza FORBIDDEN cuando el usuario no es administrador.
     */
    @Test
    void delete_notAdmin_throwsForbidden() {
        NewsService svc = mock(NewsService.class);
        NewsController c = buildController(svc);

        Authentication auth = mock(Authentication.class);
        doReturn(Collections.emptyList()).when(auth).getAuthorities();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> c.delete(1L, auth));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
