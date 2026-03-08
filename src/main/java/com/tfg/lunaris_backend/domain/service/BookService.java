package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.BookRepository;
import com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto;
import com.tfg.lunaris_backend.domain.model.Book;
import com.tfg.lunaris_backend.presentation.exceptions.BookNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    // GET
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // GET BY ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con id " + id));
    }

    // CREATE (POST)
    public Book createBook(Book book) {
        if (book.getApiId() == null || book.getApiId().isBlank()) {
            book.setApiId("custom-" + UUID.randomUUID());
        }
        return bookRepository.save(book);
    }

    // UPDATE
    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con id " + id));
        book.setTitle(bookDetails.getTitle());
        book.setCoverImage(bookDetails.getCoverImage());
        book.setDescription(bookDetails.getDescription());
        book.setAuthor(bookDetails.getAuthor());
        book.setApiId(bookDetails.getApiId());
        book.setReleaseYear(bookDetails.getReleaseYear());
        book.setScore(bookDetails.getScore());
        return bookRepository.save(book);
    }

    // DELETE
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    // SEARCH by title or author
    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }

    // Importa un libro desde Open Library a la base de datos
    public Book importFromOpenLibrary(OpenLibraryBookDto openLibraryBook) {
        // Verificar si el libro ya existe por apiId
        Optional<Book> existingBook = bookRepository.findAll().stream()
                .filter(b -> b.getApiId() != null && b.getApiId().equals(openLibraryBook.getKey()))
                .findFirst();

        if (existingBook.isPresent()) {
            return existingBook.get(); // Retornar el libro existente
        }

        // Crear nuevo libro
        Book book = new Book();
        book.setTitle(openLibraryBook.getTitle());
        book.setAuthor(openLibraryBook.getFirstAuthor());
        book.setReleaseYear(openLibraryBook.getFirstPublishYear());
        book.setCoverImage(openLibraryBook.getCoverUrl());
        book.setApiId(openLibraryBook.getKey());

        // Copiar descripción si está disponible
        book.setDescription(openLibraryBook.getDescription() != null ? openLibraryBook.getDescription() : "");

        // Copiar puntuación si está disponible, sino usar 0.0
        book.setScore(openLibraryBook.getRatingsAverage() != null ? openLibraryBook.getRatingsAverage() : 0.0);

        return bookRepository.save(book);
    }
}
