package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
