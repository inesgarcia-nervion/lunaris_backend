package com.tfg.lunaris_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.model.Saga;
import com.tfg.lunaris_backend.repository.SagaRepository;
import java.util.List;

@RestController
public class SagaController {
    @Autowired
    private SagaRepository sagaRepository;

    @GetMapping("/sagas")
    public List<Saga> getAllSagas() {
        return sagaRepository.findAll();
    }
}
