package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.SagaRepository;
import com.tfg.lunaris_backend.domain.model.Saga;
import com.tfg.lunaris_backend.presentation.exceptions.SagaNotFoundException;

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
 * Test para {@link SagaService}.
 */
@ExtendWith(MockitoExtension.class)
class SagaServiceTest {

    @Mock
    private SagaRepository repo;

    @InjectMocks
    private SagaService svc;

    /**
     * Verifica los flujos principales de {@link SagaService}.
     */
    @Test
    void flows() {
        Saga s = new Saga(); s.setName("S");
        when(repo.findAll()).thenReturn(List.of(s));
        assertFalse(svc.getAllSagas().isEmpty());

        when(repo.findById(1L)).thenReturn(Optional.of(s));
        assertSame(s, svc.getSagaById(1L));

        when(repo.save(s)).thenReturn(s);
        assertSame(s, svc.createSaga(s));

        Saga d = new Saga(); d.setName("New");
        when(repo.findById(2L)).thenReturn(Optional.of(s));
        when(repo.save(s)).thenReturn(s);
        Saga up = svc.updateSaga(2L, d);
        assertEquals("New", up.getName());

        svc.deleteSaga(3L);
        verify(repo).deleteById(3L);
    }

    /**
     * Verifica que se lanza una excepción cuando no se encuentra una saga por ID.
     * @throws Exception si ocurre un error durante el test
     */
    @Test
    void getSagaById_notFound_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(SagaNotFoundException.class,
                () -> svc.getSagaById(99L));
    }

    /**
     * Verifica que se lanza una excepción cuando se intenta actualizar una saga que no existe.
     * @throws Exception si ocurre un error durante el test
     */
    @Test
    void updateSaga_notFound_throws() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(SagaNotFoundException.class,
                () -> svc.updateSaga(99L, new Saga()));
    }
}
