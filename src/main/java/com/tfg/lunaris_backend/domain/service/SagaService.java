package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.SagaRepository;
import com.tfg.lunaris_backend.domain.model.Saga;
import com.tfg.lunaris_backend.presentation.exceptions.SagaNotFoundException;

import java.util.List;

@Service
public class SagaService {

    @Autowired
    private SagaRepository sagaRepository;

    // GET
    public List<Saga> getAllSagas() {
        return sagaRepository.findAll();
    }

    // GET BY ID
    public Saga getSagaById(Long id) {
        return sagaRepository.findById(id)
                .orElseThrow(() -> new SagaNotFoundException("Saga no encontrada con id " + id));
    }

    // CREATE (POST)
    public Saga createSaga(Saga saga) {
        return sagaRepository.save(saga);
    }

    // UPDATE
    public Saga updateSaga(Long id, Saga sagaDetails) {
        Saga saga = sagaRepository.findById(id)
                .orElseThrow(() -> new SagaNotFoundException("Saga no encontrada con id " + id));
        saga.setName(sagaDetails.getName());
        return sagaRepository.save(saga);
    }

    // DELETE
    public void deleteSaga(Long id) {
        sagaRepository.deleteById(id);
    }
}
