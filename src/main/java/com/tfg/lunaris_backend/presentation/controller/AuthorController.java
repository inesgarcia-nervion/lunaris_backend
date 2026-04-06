package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.domain.model.Author;
import com.tfg.lunaris_backend.domain.service.AuthorService;

import java.util.List;

/**
 * Controlador que maneja las operaciones relacionadas con los autores.
 * 
 * Proporciona endpoints para crear, actualizar, eliminar y obtener autores.
 */
@RestController
public class AuthorController {
    @Autowired
    private AuthorService authorService;

    /**
     * Endpoint para obtener una lista de todos los autores.
     * @return lista de autores
     */ 
    @GetMapping("/authors")
    public List<Author> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    /**
     * Endpoint para obtener un autor por su identificador.
     * @param id identificador del autor
     * @return autor encontrado
     */
    @GetMapping("/authors/{id}")
    public Author getAuthorById(@PathVariable Long id) {
        return authorService.getAuthorById(id);
    }

    /**
     * Endpoint para crear un nuevo autor. Recibe un objeto `Author` en el cuerpo de la solicitud,
     * y devuelve el autor creado.
     * @param author autor a crear
     * @return autor creado
     */
    @PostMapping("/authors")
    public Author createAuthor(@RequestBody Author author) {
        return authorService.createAuthor(author);
    }

    /**
     * Endpoint para actualizar un autor existente. Recibe el ID del autor a actualizar como parámetro
     * y un objeto `Author` con los detalles a actualizar en el cuerpo de la solicitud, y devuelve el autor actualizado.
     * @param id identificador del autor a actualizar
     * @param authorDetails detalles del autor a actualizar
     * @return autor actualizado
     */
    @PutMapping("/authors/{id}")
    public Author updateAuthor(@PathVariable Long id, @RequestBody Author authorDetails) {
        return authorService.updateAuthor(id, authorDetails);
    }

    /**
     * Endpoint para eliminar un autor por su ID.
     * @param id identificador del autor a eliminar
     */
    @DeleteMapping("/authors/{id}")
    public void deleteAuthor(@PathVariable Long id) {
        authorService.deleteAuthor(id);
    }
}