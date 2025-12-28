package com.tfg.lunaris_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.model.Book;
import com.tfg.lunaris_backend.repository.BookRepository;
import java.util.List;

@RestController
public class BookController {
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/")
    public String home() {
        return "Hola desde LunarisBackend!";
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }
}
