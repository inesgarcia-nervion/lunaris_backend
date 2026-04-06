package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.Genre;

import java.util.Optional;

/**
 * Repositorio JPA para `Genre`.
 *
 * Permite buscar géneros por nombre ignorando mayúsculas/minúsculas.
 */
public interface GenreRepository extends JpaRepository<Genre, Long> {

    /**
     * Busca un género por nombre ignorando mayúsculas/minúsculas.
     *
     * @param name nombre del género a buscar
     * @return optional con el género si existe
     */
    Optional<Genre> findByNameIgnoreCase(String name);

}
