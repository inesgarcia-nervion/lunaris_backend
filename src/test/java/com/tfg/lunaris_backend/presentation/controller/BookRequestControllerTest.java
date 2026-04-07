package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.BookRequest;
import com.tfg.lunaris_backend.domain.service.BookRequestService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class BookRequestControllerTest {
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

    @Test
    void deleteRequiresAdmin() {
        BookRequestService svc = mock(BookRequestService.class);
        BookRequestController c = new BookRequestController();
        ReflectionTestUtils.setField(c, "bookRequestService", svc);

        Authentication auth = mock(Authentication.class);
        when(auth.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        try {
            c.delete(1L, auth);
            fail("Should have thrown");
        } catch (org.springframework.web.server.ResponseStatusException ex) {
            assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        }
    }

    @Test
    void deleteAsAdmin_succeeds() {
        BookRequestService svc = mock(BookRequestService.class);
        BookRequestController c = new BookRequestController();
        ReflectionTestUtils.setField(c, "bookRequestService", svc);

        Authentication auth = mock(Authentication.class);
        doReturn(java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(auth).getAuthorities();

        c.delete(2L, auth);
        verify(svc).delete(2L);
    }
}
