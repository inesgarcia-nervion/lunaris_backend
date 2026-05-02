package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.domain.model.News;
import java.util.List;

/**
 * Repositorio JPA para {@link News}.
 */
public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByOrderByIdDesc();
}
