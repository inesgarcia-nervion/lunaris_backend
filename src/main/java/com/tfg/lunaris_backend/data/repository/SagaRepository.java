package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tfg.lunaris_backend.domain.model.Saga;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad `Saga`.
 *
 * Proporciona consultas para localizar sagas por nombre y por título de libro miembro.
 */
public interface SagaRepository extends JpaRepository<Saga, Long> {

    /**
     * Busca una saga por su nombre exacto.
     *
     * @param name nombre de la saga
     * @return optional con la saga si existe
     */
    Optional<Saga> findByName(String name);

    /**
     * Busca una saga que contenga un libro con el título dado (ignorando mayúsculas).
     *
     * @param title título del libro que pertenezca a la saga
     * @return optional con la saga si se encuentra
     */
    @Query("SELECT DISTINCT s FROM Saga s JOIN s.books sb WHERE LOWER(sb.title) = LOWER(:title)")
    Optional<Saga> findByBookTitleIgnoreCase(@Param("title") String title);
}
