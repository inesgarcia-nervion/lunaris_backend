package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.GenreRepository;
import com.tfg.lunaris_backend.domain.model.Genre;
import com.tfg.lunaris_backend.presentation.exceptions.GenreNotFoundException;

import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con los géneros.
 * 
 * Proporciona métodos para obtener, crear, actualizar y eliminar géneros.
 */
@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    /**
     * Obtiene una lista de todos los géneros.
     * @return lista de géneros
     */
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    /**
     * Obtiene un género por su identificador.
     * @param id identificador del género
     * @return género encontrado
     * @throws GenreNotFoundException si no se encuentra el género con el id proporcionado
     */
    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Género no encontrado con id " + id));
    }

    /**
     * Crea un nuevo género.
     * @param genre género a crear
     * @return género creado
     */
    public Genre createGenre(Genre genre) {
        return genreRepository.findByNameIgnoreCase(genre.getName())
                .orElseGet(() -> genreRepository.save(genre));
    }

    /**
     * Actualiza un género existente.
     * @param id identificador del género a actualizar
     * @param genreDetails detalles del género a actualizar
     * @return género actualizado
     * @throws GenreNotFoundException si no se encuentra el género con el id proporcionado
     */
    public Genre updateGenre(Long id, Genre genreDetails) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Género no encontrado con id " + id));
        genre.setName(genreDetails.getName());
        return genreRepository.save(genre);
    }

    /**
     * Elimina un género por su identificador.
     * @param id identificador del género a eliminar
     */
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}
