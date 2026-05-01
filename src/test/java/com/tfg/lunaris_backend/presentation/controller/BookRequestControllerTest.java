package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.BookRequest;
import com.tfg.lunaris_backend.domain.service.BookRequestService;
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
 * Test para {@link BookRequestController}.
 */
class BookRequestControllerTest {

    /**
     * Verifica que los métodos del controlador delegan correctamente en el servicio.
     */
    @Test
    void getAllAndCreate() {
        BookRequestService svc = mock(BookRequestService.class);
        BookRequestController c = new BookRequestController();
        ReflectionTestUtils.setField(c, "bookRequestService", svc);

        BookRequest br = new BookRequest();
        br.setId(9L);
        when(svc.getAll()).thenReturn(List.of(br));
        assertEquals(1, c.getAll().size());

        when(svc.create(br)).thenReturn(br);
        assertEquals(br, c.create(br));
    }

    /**
     * Verifica que la eliminación requiere permisos de administrador.
     */
    @Test
    void deleteRequiresAdmin() {
        BookRequestService svc = mock(BookRequestService.class);
        BookRequestController c = new BookRequestController();
        ReflectionTestUtils.setField(c, "bookRequestService", svc);

        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());

        try {
            c.delete(1L, auth);
            fail("Should have thrown");
        } catch (ResponseStatusException ex) {
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        }
    }

    /**
     * Verifica que la eliminación como administrador tiene éxito.
     */
    @Test
    void deleteAsAdmin_succeeds() {
        BookRequestService svc = mock(BookRequestService.class);
        BookRequestController c = new BookRequestController();
        ReflectionTestUtils.setField(c, "bookRequestService", svc);

        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(auth).getAuthorities();

        c.delete(2L, auth);
        verify(svc).delete(2L);
    }
}
