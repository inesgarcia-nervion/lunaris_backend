package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.AuthorRepository;
import com.tfg.lunaris_backend.domain.model.Author;
import com.tfg.lunaris_backend.presentation.exceptions.AuthorNotFoundException;

import java.util.List;

@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    // GET
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    // GET BY ID
    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Autor no encontrado con id " + id));
    }

    // CREATE (POST)
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    // UPDATE
    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Autor no encontrado con id " + id));
        author.setName(authorDetails.getName());
        author.setBooks(authorDetails.getBooks());
        return authorRepository.save(author);
    }

    // DELETE
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

}
