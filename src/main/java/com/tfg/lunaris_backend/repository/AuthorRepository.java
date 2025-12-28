package com.tfg.lunaris_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

}
