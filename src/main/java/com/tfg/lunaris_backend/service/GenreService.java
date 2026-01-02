package com.tfg.lunaris_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.exceptions.GenreNotFoundException;
import com.tfg.lunaris_backend.model.Genre;
import com.tfg.lunaris_backend.repository.GenreRepository;
import java.util.List;

@Service
public class GenreService {

    @Autowired
    private GenreRepository genreRepository;

    // GET
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    // GET BY ID
    public Genre getGenreById(Long id) {
        return genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Género no encontrado con id " + id));
    }

    // CREATE (POST)
    public Genre createGenre(Genre genre) {
        return genreRepository.save(genre);
    }

    // UPDATE
    public Genre updateGenre(Long id, Genre genreDetails) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Género no encontrado con id " + id));
        genre.setName(genreDetails.getName());
        return genreRepository.save(genre);
    }

    // DELETE
    public void deleteGenre(Long id) {
        genreRepository.deleteById(id);
    }
}
