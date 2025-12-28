package com.tfg.lunaris_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.model.Genre;

public interface GenreRepository extends JpaRepository<Genre, Long> {

}
