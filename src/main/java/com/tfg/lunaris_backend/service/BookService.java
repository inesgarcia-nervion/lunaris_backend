package com.tfg.lunaris_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.exceptions.BookNotFoundException;
import com.tfg.lunaris_backend.model.Book;
import com.tfg.lunaris_backend.repository.BookRepository;
import java.util.List;

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
}
