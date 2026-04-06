package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.domain.model.BookRequest;

/**
 * Repositorio JPA para `BookRequest`.
 *
 * Gestiona las solicitudes de libros realizadas por usuarios.
 */
public interface BookRequestRepository extends JpaRepository<BookRequest, Long> {

}
