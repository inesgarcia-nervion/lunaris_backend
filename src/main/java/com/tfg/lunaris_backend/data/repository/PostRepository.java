package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.domain.model.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByIdDesc();
}
