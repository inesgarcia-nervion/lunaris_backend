package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.ReviewRepository;
import com.tfg.lunaris_backend.domain.model.Review;
import com.tfg.lunaris_backend.presentation.exceptions.ReviewNotFoundException;

import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    private final RestTemplate rest = new RestTemplate();

    // GET (newest first)
    public List<Review> getAllReviews() {
        List<Review> reviews = reviewRepository.findAllByOrderByIdDesc();
        enrichMissingBookData(reviews);
        return reviews;
    }

    // GET BY BOOK API ID
    public List<Review> getReviewsByBookApiId(String bookApiId) {
        List<Review> reviews = reviewRepository.findByBookApiId(bookApiId);
        enrichMissingBookData(reviews);
        return reviews;
    }

    // For reviews that reference OpenLibrary works but lack title/cover, fetch and persist them
    private void enrichMissingBookData(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) return;
        for (Review r : reviews) {
            try {
                if (r.getBookApiId() == null) continue;
                boolean missingTitle = r.getBookTitle() == null || r.getBookTitle().isBlank();
                boolean missingCover = r.getCoverUrl() == null || r.getCoverUrl().isBlank();
                if (!missingTitle && !missingCover) continue;
                String apiId = r.getBookApiId();
                // normalize: accept "/works/OL...W" or "works/OL...W" or just "OL...W"
                String workKey = apiId;
                if (workKey.startsWith("/")) workKey = workKey.substring(1);
                if (workKey.startsWith("works/")) workKey = workKey.substring("works/".length());
                // Only handle OpenLibrary works (OL...W)
                if (!workKey.startsWith("OL") || !workKey.endsWith("W")) continue;
                String url = "https://openlibrary.org/works/" + workKey + ".json";
                ResponseEntity<Map> resp = rest.getForEntity(url, Map.class);
                if (resp.getStatusCode() != HttpStatus.OK || resp.getBody() == null) continue;
                Map body = resp.getBody();
                if (missingTitle) {
                    Object title = body.get("title");
                    if (title instanceof String) r.setBookTitle((String) title);
                }
                if (missingCover) {
                    Object covers = body.get("covers");
                    if (covers instanceof java.util.List && !((java.util.List) covers).isEmpty()) {
                        Object first = ((java.util.List) covers).get(0);
                        String coverUrl = null;
                        if (first != null) {
                            coverUrl = "https://covers.openlibrary.org/b/id/" + first.toString() + "-M.jpg";
                        }
                        if (coverUrl != null) r.setCoverUrl(coverUrl);
                    } else {
                        // fallback to covers by work key
                        r.setCoverUrl("https://covers.openlibrary.org/b/works/" + workKey + "-M.jpg");
                    }
                }
                // persist any changes
                reviewRepository.save(r);
            } catch (Exception e) {
                // ignore per-item failures to avoid breaking the whole response
            }
        }
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
        if (reviewDetails.getBookTitle() != null)
            review.setBookTitle(reviewDetails.getBookTitle());
        if (reviewDetails.getCoverUrl() != null)
            review.setCoverUrl(reviewDetails.getCoverUrl());
        return reviewRepository.save(review);
    }

    // DELETE
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
