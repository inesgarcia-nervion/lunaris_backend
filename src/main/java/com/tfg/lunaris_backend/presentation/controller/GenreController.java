package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.domain.model.Genre;
import com.tfg.lunaris_backend.domain.service.GenreService;

import java.util.List;

/**
 * Controlador que maneja las operaciones relacionadas con los géneros.
 * 
 * Proporciona endpoints para crear, actualizar, eliminar y obtener géneros.
 */
@RestController
public class GenreController {
    @Autowired
    private GenreService genreService;

    /**
     * Endpoint para obtener todos los géneros.
     * @return lista de géneros
     */
    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    /**
     * Endpoint para obtener un género por su ID.
     * @param id identificador del género
     * @return género encontrado
     */
    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable Long id) {
        return genreService.getGenreById(id);
    }

    /**
     * Endpoint para crear un nuevo género. Recibe un objeto `Genre` con 
     * los datos necesarios para crear el género, y devuelve el género creado.
     * @param genre objeto con los datos para crear el género
     * @return género creado
     */
    @PostMapping("/genres")
    public Genre createGenre(@RequestBody Genre genre) {
        return genreService.createGenre(genre);
    }

    /**
     * Endpoint para actualizar un género existente. Recibe el ID del género a 
     * actualizar y un objeto `Genre` con los detalles a actualizar.
     * @param id identificador del género a actualizar
     * @param genreDetails detalles del género a actualizar
     * @return género actualizado
     */
    @PutMapping("/genres/{id}")
    public Genre updateGenre(@PathVariable Long id, @RequestBody Genre genreDetails) {
        return genreService.updateGenre(id, genreDetails);
    }

    /**
     * Endpoint para eliminar un género por su ID.
      * @param id identificador del género a eliminar
      */
    @DeleteMapping("/genres/{id}")
    public void deleteGenre(@PathVariable Long id) {
        genreService.deleteGenre(id);
    }
}
