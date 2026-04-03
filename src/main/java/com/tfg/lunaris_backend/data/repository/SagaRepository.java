package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.tfg.lunaris_backend.domain.model.Saga;

import java.util.Optional;

public interface SagaRepository extends JpaRepository<Saga, Long> {

    Optional<Saga> findByName(String name);

    @Query("SELECT DISTINCT s FROM Saga s JOIN s.books sb WHERE LOWER(sb.title) = LOWER(:title)")
    Optional<Saga> findByBookTitleIgnoreCase(@Param("title") String title);
}
