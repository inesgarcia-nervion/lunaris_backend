package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.domain.dto.SagaScrapedDto;
import com.tfg.lunaris_backend.domain.model.Saga;
import com.tfg.lunaris_backend.domain.service.SagaScrapingService;
import com.tfg.lunaris_backend.domain.service.SagaService;

import java.util.List;

/**
 * Controlador que maneja las operaciones relacionadas con las sagas.
 * 
 * Proporciona endpoints para crear, obtener, actualizar y eliminar sagas.
 */
@RestController
public class SagaController {
    @Autowired
    private SagaService sagaService;

    @Autowired
    private SagaScrapingService sagaScrapingService;

    /**
     * Endpoint para realizar web scraping de una saga en función de su título y autor. 
     * Recibe los parámetros `title` y `author` (opcional) y devuelve un objeto con los 
     * datos extraídos de la saga. Si no se encuentra información, devuelve una respuesta sin contenido.
     * @param title título de la saga
     * @param author autor de la saga (opcional)
     * @return objeto con los datos extraídos de la saga o respuesta sin contenido si no se encuentra información
     */
    @GetMapping("/api/saga/scrape")
    public ResponseEntity<SagaScrapedDto> scrapeSaga(
            @RequestParam String title,
            @RequestParam(required = false) String author) {
        SagaScrapedDto result = sagaScrapingService.scrapeSaga(title, author);
        if (result == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * Endpoint para obtener todas las sagas. Devuelve una lista de todas las sagas disponibles.
     * @return lista de sagas
     */
    @GetMapping("/sagas")
    public List<Saga> getAllSagas() {
        return sagaService.getAllSagas();
    }

    /**
     * Endpoint para obtener una saga por su ID. Devuelve la saga correspondiente si existe.
     * @param id identificador de la saga
     * @return saga encontrada
     */
    @GetMapping("/sagas/{id}")
    public Saga getSagaById(@PathVariable Long id) {
        return sagaService.getSagaById(id);
    }

    /**
     * Endpoint para crear una nueva saga. Recibe un objeto con los datos de la saga a 
     * crear y devuelve la saga creada.
     * @param saga objeto con los datos de la saga a crear
     * @return saga creada
     */
    @PostMapping("/sagas")
    public Saga createSaga(@RequestBody Saga saga) {
        return sagaService.createSaga(saga);
    }

    /**
     * Endpoint para actualizar una saga existente. Recibe el ID de la saga a actualizar 
     * y un objeto con los datos a actualizar, y devuelve la saga actualizada.
     * @param id identificador de la saga a actualizar
     * @param sagaDetails objeto con los datos de la saga a actualizar
     * @return saga actualizada
     */
    @PutMapping("/sagas/{id}")
    public Saga updateSaga(@PathVariable Long id, @RequestBody Saga sagaDetails) {
        return sagaService.updateSaga(id, sagaDetails);
    }

    /**
     * Endpoint para eliminar una saga por su ID. Recibe el ID de la saga a eliminar 
     * y elimina la saga correspondiente.
     * @param id identificador de la saga a eliminar
     */
    @DeleteMapping("/sagas/{id}")
    public void deleteSaga(@PathVariable Long id) {
        sagaService.deleteSaga(id);
    }
}
