package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.domain.dto.BookCreateRequest;
import com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto;
import com.tfg.lunaris_backend.domain.model.Book;
import com.tfg.lunaris_backend.domain.service.BookService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/books")
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookService.getAllBooks(pageable);
    }

    @GetMapping("/books/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @PostMapping("/books")
    public Book createBook(@RequestBody BookCreateRequest request) {
        return bookService.createBook(request);
    }

    @PutMapping("/books/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookService.updateBook(id, bookDetails);
    }

    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @GetMapping("/books/search")
    public Page<Book> searchBooks(@RequestParam("q") String query, Pageable pageable) {
        return bookService.searchBooks(query, pageable);
    }

    @GetMapping("/books/by-api-id")
    public org.springframework.http.ResponseEntity<Book> getByApiId(@RequestParam("apiId") String apiId) {
        return bookService.findByApiId(apiId)
                .map(org.springframework.http.ResponseEntity::ok)
                .orElse(org.springframework.http.ResponseEntity.notFound().build());
    }

    // Importar libro desde Open Library a la base de datos
    @PostMapping("/books/import/openlibrary")
    public Book importFromOpenLibrary(@RequestBody OpenLibraryBookDto openLibraryBook) {
        return bookService.importFromOpenLibrary(openLibraryBook);
    }

}
