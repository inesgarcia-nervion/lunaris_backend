package com.tfg.lunaris_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.model.Saga;
import com.tfg.lunaris_backend.service.SagaService;
import java.util.List;

@RestController
public class SagaController {
    @Autowired
    private SagaService sagaService;

    @GetMapping("/sagas")
    public List<Saga> getAllSagas() {
        return sagaService.getAllSagas();
    }

    @GetMapping("/sagas/{id}")
    public Saga getSagaById(@PathVariable Long id) {
        return sagaService.getSagaById(id);
    }

    @PostMapping("/sagas")
    public Saga createSaga(@RequestBody Saga saga) {
        return sagaService.createSaga(saga);
    }

    @PutMapping("/sagas/{id}")
    public Saga updateSaga(@PathVariable Long id, @RequestBody Saga sagaDetails) {
        return sagaService.updateSaga(id, sagaDetails);
    }

    @DeleteMapping("/sagas/{id}")
    public void deleteSaga(@PathVariable Long id) {
        sagaService.deleteSaga(id);
    }
}
