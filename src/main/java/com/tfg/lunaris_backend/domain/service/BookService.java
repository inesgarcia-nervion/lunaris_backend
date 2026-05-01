package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.BookRepository;
import com.tfg.lunaris_backend.data.repository.GenreRepository;
import com.tfg.lunaris_backend.domain.dto.BookCreateRequest;
import com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto;
import com.tfg.lunaris_backend.domain.model.Book;
import com.tfg.lunaris_backend.domain.model.Saga;
import com.tfg.lunaris_backend.domain.model.SagaBook;
import com.tfg.lunaris_backend.domain.model.Genre;
import com.tfg.lunaris_backend.presentation.exceptions.BookNotFoundException;
import com.tfg.lunaris_backend.presentation.exceptions.DuplicateBookException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio que maneja la lógica de negocio relacionada con los libros.
 * 
 * Proporciona métodos para obtener, crear, actualizar, eliminar y buscar libros.
 */
@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private com.tfg.lunaris_backend.data.repository.SagaRepository sagaRepository;

    @Autowired
    private GenreRepository genreRepository;

    /**
     * Obtiene una lista de todos los libros.
     * @return lista de libros
     */
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * Obtiene una página de libros con paginación.
     * @param pageable información de paginación
     * @return página de libros
     */
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    /**
     * Obtiene un libro por su identificador.
     * @param id identificador del libro
     * @return libro encontrado
     * @throws BookNotFoundException si no se encuentra el libro con el id proporcionado
     */
    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Libro no encontrado con id " + id));
    }

    /**
     * Crea un nuevo libro.
     * @param request solicitud de creación de libro
     * @return libro creado
     */
    public Book createBook(BookCreateRequest request) {
        if (bookRepository.findByTitleIgnoreCaseAndAuthorIgnoreCase(
                request.getTitle(), request.getAuthor()).isPresent()) {
            throw new DuplicateBookException(
                "Ya existe un libro con el título '" + request.getTitle() +
                "' y el autor '" + request.getAuthor() + "'");
        }
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

        Book saved = bookRepository.save(book);

        // Si viene sagaId enlazar al libro creando una entrada SagaBook en la saga existente
        if (request.getSagaId() != null) {
            sagaRepository.findById(request.getSagaId()).ifPresent(saga -> {
                SagaBook sb = new SagaBook();
                sb.setTitle(saved.getTitle() != null ? saved.getTitle().trim() : null);
                sb.setAuthor(saved.getAuthor() != null ? saved.getAuthor().trim() : null);
                sb.setYear(saved.getReleaseYear());
                sb.setSaga(saga);
                saga.getBooks().add(sb);
                sagaRepository.save(saga);
            });
        } else if (request.getSagaName() != null && !request.getSagaName().isBlank()) {
            String name = request.getSagaName().trim();
            Saga saga = sagaRepository.findByName(name).orElseGet(() -> {
                Saga s = new Saga();
                s.setName(name);
                return s;
            });
            SagaBook sb = new SagaBook();
            sb.setTitle(saved.getTitle() != null ? saved.getTitle().trim() : null);
            sb.setAuthor(saved.getAuthor() != null ? saved.getAuthor().trim() : null);
            sb.setYear(saved.getReleaseYear());
            sb.setSaga(saga);
            saga.getBooks().add(sb);
            sagaRepository.save(saga);
        }

        return saved;
    }

    /**
     * Actualiza un libro existente.
     * @param id identificador del libro a actualizar
     * @param bookDetails detalles del libro a actualizar
     * @return libro actualizado
     * @throws BookNotFoundException si no se encuentra el libro con el id proporcionado
     */
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

    /**
     * Elimina un libro por su identificador.
     * @param id identificador del libro a eliminar
     */
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    /**
     * Busca libros cuyo título o autor contenga el texto dado (ignorando mayúsculas).
     * @param query texto a buscar en el título o autor
     * @return lista de libros que coinciden con la búsqueda
     */
    public List<Book> searchBooks(String query) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query);
    }

    /**
     * Busca libros cuyo título o autor contenga el texto dado (ignorando mayúsculas) con paginación.
     * @param query texto a buscar en el título o autor
     * @param pageable información de paginación
     * @return página de libros que coinciden con la búsqueda
     */
    public Page<Book> searchBooks(String query, Pageable pageable) {
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(query, query, pageable);
    }

    /**
     * Busca un libro por su identificador de API.
     * @param apiId identificador de API del libro
     * @return libro encontrado o vacío si no se encuentra
     */
    public Optional<Book> findByApiId(String apiId) {
        return bookRepository.findByApiId(apiId);
    }

    /**
     * Importa un libro desde un DTO de Open Library. Si el libro ya existe (basado en apiId), lo devuelve sin crear uno nuevo.
     * @param openLibraryBook DTO con los datos del libro de Open Library
     * @return libro importado o existente
     */
    public Book importFromOpenLibrary(OpenLibraryBookDto openLibraryBook) {
        Optional<Book> existingBook = bookRepository.findAll().stream()
                .filter(b -> b.getApiId() != null && b.getApiId().equals(openLibraryBook.getKey()))
                .findFirst();

        if (existingBook.isPresent()) {
            return existingBook.get(); 
        }

        Book book = new Book();
        book.setTitle(openLibraryBook.getTitle());
        book.setAuthor(openLibraryBook.getFirstAuthor());
        book.setReleaseYear(openLibraryBook.getFirstPublishYear());
        book.setCoverImage(openLibraryBook.getCoverUrl());
        book.setApiId(openLibraryBook.getKey());

        book.setDescription(openLibraryBook.getDescription() != null ? openLibraryBook.getDescription() : "");

        book.setScore(openLibraryBook.getRatingsAverage() != null ? openLibraryBook.getRatingsAverage() : 0.0);

        return bookRepository.save(book);
    }
}
