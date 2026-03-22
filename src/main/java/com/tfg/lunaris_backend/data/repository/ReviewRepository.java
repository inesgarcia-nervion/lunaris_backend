package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	java.util.List<Review> findByBookApiId(String bookApiId);

	java.util.List<Review> findAllByOrderByIdDesc();

}
