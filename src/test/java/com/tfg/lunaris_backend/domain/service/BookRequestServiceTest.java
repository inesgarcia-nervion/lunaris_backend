package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.BookRequestRepository;
import com.tfg.lunaris_backend.domain.model.BookRequest;
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
 * Test para la clase BookRequestService.
 */
@ExtendWith(MockitoExtension.class)
class BookRequestServiceTest {

    @Mock
    private BookRequestRepository repo;

    @InjectMocks
    private BookRequestService svc;

    /**
     * Verifica que se pueden obtener, crear y eliminar solicitudes de libros correctamente.
     */
    @Test
    void getAllAndGetByIdAndCreateAndDelete() {
        BookRequest br = new BookRequest("T","A");
        when(repo.findAll()).thenReturn(List.of(br));
        assertEquals(1, svc.getAll().size());

        when(repo.findById(1L)).thenReturn(Optional.of(br));
        assertNotNull(svc.getById(1L));

        when(repo.save(br)).thenReturn(br);
        assertSame(br, svc.create(br));

        svc.delete(2L);
        verify(repo).deleteById(2L);
    }
}
