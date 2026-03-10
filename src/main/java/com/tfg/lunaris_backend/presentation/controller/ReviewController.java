package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.lunaris_backend.domain.model.Review;
import com.tfg.lunaris_backend.domain.service.ReviewService;

import java.util.List;

@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    @GetMapping("/reviews")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    @GetMapping("/reviews/book")
    public java.util.List<Review> getReviewsByBookApiId(@org.springframework.web.bind.annotation.RequestParam("apiId") String apiId) {
        return reviewService.getReviewsByBookApiId(apiId);
    }

    @PostMapping("/reviews")
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    @PutMapping("/reviews/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review reviewDetails) {
        return reviewService.updateReview(id, reviewDetails);
    }

    @DeleteMapping("/reviews/{id}")
    public void deleteReview(@PathVariable Long id, Authentication auth) {
        Review review = reviewService.getReviewById(id);
        String currentUser = auth != null ? auth.getName() : null;
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (isAdmin || (currentUser != null && currentUser.equals(review.getUsername()))) {
            reviewService.deleteReview(id);
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado a eliminar esta reseña");
    }
}
