package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.SagaRepository;
import com.tfg.lunaris_backend.domain.model.Saga;
import com.tfg.lunaris_backend.presentation.exceptions.SagaNotFoundException;

import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con las sagas.
 * 
 * Proporciona métodos para crear, actualizar, eliminar y obtener sagas.
 */
@Service
public class SagaService {

    @Autowired
    private SagaRepository sagaRepository;

    /**
     * Obtiene todas las sagas.
     * @return lista de sagas
     */
    public List<Saga> getAllSagas() {
        return sagaRepository.findAll();
    }

    /**
     * Obtiene una saga por su ID.
     * @param id ID de la saga
     * @return saga encontrada
     * @throws SagaNotFoundException si la saga no existe
     */
    public Saga getSagaById(Long id) {
        return sagaRepository.findById(id)
                .orElseThrow(() -> new SagaNotFoundException("Saga no encontrada con id " + id));
    }

    /**
     * Crea una nueva saga.
     * @param saga saga a crear 
     * @return saga creada
     */
    public Saga createSaga(Saga saga) {
        return sagaRepository.save(saga);
    }

    /**
     * Actualiza una saga existente.
     * @param id ID de la saga a actualizar
     * @param sagaDetails detalles de la saga a actualizar
     * @return saga actualizada
     * @throws SagaNotFoundException si la saga no existe
     */
    public Saga updateSaga(Long id, Saga sagaDetails) {
        Saga saga = sagaRepository.findById(id)
                .orElseThrow(() -> new SagaNotFoundException("Saga no encontrada con id " + id));
        saga.setName(sagaDetails.getName());
        return sagaRepository.save(saga);
    }

    /**
     * Elimina una saga por su ID.
     * @param id ID de la saga a eliminar
     */
    public void deleteSaga(Long id) {
        sagaRepository.deleteById(id);
    }
}
