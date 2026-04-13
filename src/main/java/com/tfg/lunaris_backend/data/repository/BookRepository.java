package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.Book;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Repositorio JPA para `Book`.
 *
 * Proporciona búsquedas por título/autor (con paginación) y consulta por id externo.
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Busca libros cuyo título o autor contenga el texto dado (ignorando mayúsculas).
     *
     * @param title  texto a buscar en el título
     * @param author texto a buscar en el autor
     * @return lista de libros que coinciden con la búsqueda
     */
    List<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author);

    /**
     * Versión paginada de la búsqueda por título o autor.
     *
     * @param title    texto a buscar en el título
     * @param author   texto a buscar en el autor
     * @param pageable información de paginación
     * @return página de resultados que coinciden con la búsqueda
     */
    Page<Book> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(String title, String author,
            Pageable pageable);

    /**
     * Busca un libro por su identificador externo (API).
     *
     * @param apiId identificador externo del libro
     * @return optional con el libro si existe
     */
    Optional<Book> findByApiId(String apiId);

    /**
     * Busca un libro por título y autor (sin distinguir mayúsculas/minúsculas).
     *
     * @param title  título del libro
     * @param author autor del libro
     * @return optional con el libro si existe
     */
    Optional<Book> findByTitleIgnoreCaseAndAuthorIgnoreCase(String title, String author);

}
