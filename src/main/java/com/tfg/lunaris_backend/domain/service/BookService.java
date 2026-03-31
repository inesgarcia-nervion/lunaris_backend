package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.BookRepository;
import com.tfg.lunaris_backend.data.repository.GenreRepository;
import com.tfg.lunaris_backend.domain.dto.BookCreateRequest;
import com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto;
import com.tfg.lunaris_backend.domain.model.Book;
import com.tfg.lunaris_backend.domain.model.Genre;
import com.tfg.lunaris_backend.presentation.exceptions.BookNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private GenreRepository genreRepository;

    // GET
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // GET - paginated
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    // GET BY ID
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con id " + id));
    }

    // CREATE (POST)
    public Book createBook(BookCreateRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setDescription(request.getDescription());
        book.setCoverImage(request.getCoverImage());
        book.setReleaseYear(request.getReleaseYear());
        book.setScore(request.getScore());
        book.setApiId(request.getApiId() != null && !request.getApiId().isBlank()
                ? request.getApiId()
                : "custom-" + UUID.randomUUID());

        if (request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
            List<Genre> genres = new ArrayList<>();
            for (Long genreId : request.getGenreIds()) {
                genreRepository.findById(genreId).ifPresent(genres::add);
            }
            book.setGenres(genres);
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

    // SEARCH paginated
    public Page<Book> searchBooks(String query, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query, pageable);
    }

    public Optional<Book> findByApiId(String apiId) {
        return bookRepository.findByApiId(apiId);
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
