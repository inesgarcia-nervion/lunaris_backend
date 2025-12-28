package com.tfg.lunaris_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.model.Genre;
import com.tfg.lunaris_backend.repository.GenreRepository;
import java.util.List;

@RestController
public class GenreController {
    @Autowired
    private GenreRepository genreRepository;

    @GetMapping("/")
    public String home() {
        return "Hola desde LunarisBackend!";
    }

    @GetMapping("/genres")
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
