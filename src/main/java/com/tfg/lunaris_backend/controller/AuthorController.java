package com.tfg.lunaris_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.model.Author;
import com.tfg.lunaris_backend.repository.AuthorRepository;
import java.util.List;

@RestController
public class AuthorController {
    @Autowired
    private AuthorRepository authorRepository;

    @GetMapping("/")
    public String home() {
        return "Hola desde LunarisBackend!";
    }

    @GetMapping("/authors")
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }
}
