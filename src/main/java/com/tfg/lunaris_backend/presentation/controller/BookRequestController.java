package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.lunaris_backend.domain.model.BookRequest;
import com.tfg.lunaris_backend.domain.service.BookRequestService;

import java.util.List;

/**
 * Controlador que maneja las operaciones relacionadas con las solicitudes de libros.
 * 
 * Proporciona endpoints para crear, eliminar y obtener solicitudes de libros.
 */
@RestController
public class BookRequestController {
    @Autowired
    private BookRequestService bookRequestService;

    /**
     * Endpoint para obtener todas las solicitudes de libros.
     * @return lista de solicitudes de libros
     */
    @GetMapping("/requests")
    public List<BookRequest> getAll() {
        return bookRequestService.getAll();
    }

    /**
    * Endpoint para crear una nueva solicitud de libro. Recibe un objeto `BookRequest` con 
    * los datos necesarios para crear la solicitud, y devuelve la solicitud creada.
    * @param br objeto con los datos para crear la solicitud de libro
    * @return solicitud de libro creada
    */  
    @PostMapping("/requests")
    public BookRequest create(@RequestBody BookRequest br) {
        return bookRequestService.create(br);
    }

    /**
    * Endpoint para eliminar una solicitud de libro por su ID. Solo los usuarios con rol ADMIN pueden eliminar solicitudes.
    * @param id identificador de la solicitud de libro a eliminar
    * @param auth información de autenticación del usuario que realiza la solicitud
    * @throws ResponseStatusException con estado 403 si el usuario no tiene permisos para eliminar la solicitud
    */
    @DeleteMapping("/requests/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        bookRequestService.delete(id);
    }
}
