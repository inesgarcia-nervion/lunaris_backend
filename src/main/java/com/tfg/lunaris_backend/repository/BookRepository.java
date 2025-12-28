package com.tfg.lunaris_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
