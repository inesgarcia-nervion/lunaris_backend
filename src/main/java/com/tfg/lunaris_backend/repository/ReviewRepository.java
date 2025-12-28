package com.tfg.lunaris_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

}
