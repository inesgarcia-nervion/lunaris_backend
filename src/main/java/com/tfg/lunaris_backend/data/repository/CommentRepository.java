package com.tfg.lunaris_backend.data.repository;

import com.tfg.lunaris_backend.domain.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para {@link Comment}.
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
