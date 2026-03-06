package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.ReviewRepository;
import com.tfg.lunaris_backend.domain.model.Review;
import com.tfg.lunaris_backend.presentation.exceptions.ReviewNotFoundException;

import java.util.List;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    // GET
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    // GET BY ID
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con id " + id));
    }

    // CREATE (POST)
    public Review createReview(Review review) {
        return reviewRepository.save(review);
    }

    // UPDATE
    public Review updateReview(Long id, Review reviewDetails) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con id " + id));
        review.setComment(reviewDetails.getComment());
        review.setRating(reviewDetails.getRating());
        review.setDate(reviewDetails.getDate());
        return reviewRepository.save(review);
    }

    // DELETE
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
