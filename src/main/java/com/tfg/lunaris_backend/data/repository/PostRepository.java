package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.domain.model.Post;
import java.util.List;

/**
 * Repositorio JPA para `Post`.
 *
 * Proporciona métodos para recuperar publicaciones, incluyendo orden descendente.
 */
public interface PostRepository extends JpaRepository<Post, Long> {
    /**
     * Recupera todas las publicaciones ordenadas por identificador de forma descendente.
     *
     * @return lista de publicaciones ordenada por id desc
     */
    List<Post> findAllByOrderByIdDesc();
}
