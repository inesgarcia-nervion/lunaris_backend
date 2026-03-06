package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
