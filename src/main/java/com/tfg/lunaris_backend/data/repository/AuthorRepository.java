package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.Author;

/**
 * Repositorio JPA para `Author`.
 *
 * Gestiona operaciones persistentes sobre autores.
 */
public interface AuthorRepository extends JpaRepository<Author, Long> {

}
