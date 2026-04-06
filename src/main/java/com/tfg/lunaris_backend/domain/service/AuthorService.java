package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.AuthorRepository;
import com.tfg.lunaris_backend.domain.model.Author;
import com.tfg.lunaris_backend.presentation.exceptions.AuthorNotFoundException;

import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con los autores.
 * 
 * Proporciona métodos para obtener, crear, actualizar y eliminar autores.
 */
@Service
public class AuthorService {
    @Autowired
    private AuthorRepository authorRepository;

    /**
     * Obtiene una lista de todos los autores.
     * @return lista de autores
     */
    public List<Author> getAllAuthors() {
        return authorRepository.findAll();
    }

    /**
     * Obtiene un autor por su identificador.
     * @param id identificador del autor
     * @return autor encontrado
     * @throws AuthorNotFoundException si no se encuentra el autor con el id proporcionado
     */
    public Author getAuthorById(Long id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Autor no encontrado con id " + id));
    }

    /**
     * Crea un nuevo autor.
     * @param author autor a crear
     * @return autor creado
     */
    public Author createAuthor(Author author) {
        return authorRepository.save(author);
    }

    /**
     * Actualiza un autor existente.
     * @param id identificador del autor a actualizar
     * @param authorDetails detalles del autor a actualizar
     * @return autor actualizado
     * @throws AuthorNotFoundException si no se encuentra el autor con el id proporcionado
     */
    public Author updateAuthor(Long id, Author authorDetails) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Autor no encontrado con id " + id));
        author.setName(authorDetails.getName());
        author.setBooks(authorDetails.getBooks());
        return authorRepository.save(author);
    }

    /**
    * Elimina un autor por su identificador.
    * @param id identificador del autor a eliminar
    */
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

}
