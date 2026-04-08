package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.ReviewRepository;
import com.tfg.lunaris_backend.domain.model.Review;
import com.tfg.lunaris_backend.presentation.exceptions.ReviewNotFoundException;

import java.util.List;
import java.util.Map;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

/**
 * Servicio que maneja la lógica de negocio relacionada con las reseñas.
 * 
 * Proporciona métodos para crear, obtener y eliminar reseñas.
 */
@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RestTemplate rest;

    /**
     * Obtiene todas las reseñas ordenadas por ID de forma descendente.
     * Si alguna reseña tiene datos de libro faltantes, intenta enriquecerlos consultando la API de Open Library.
     * @return lista de reseñas
     */
    public List<Review> getAllReviews() {
        List<Review> reviews = reviewRepository.findAllByOrderByIdDesc();
        enrichMissingBookData(reviews);
        return reviews;
    }

    /**
     * Obtiene todas las reseñas asociadas a un identificador de libro externo.
     * Si alguna reseña tiene datos de libro faltantes, intenta enriquecerlos consultando la API de Open Library.
     * @param bookApiId identificador externo del libro
     * @return lista de reseñas para el libro
     */
    public List<Review> getReviewsByBookApiId(String bookApiId) {
        List<Review> reviews = reviewRepository.findByBookApiId(bookApiId);
        enrichMissingBookData(reviews);
        return reviews;
    }

    /**
     * Enriquecer los datos faltantes de los libros en las reseñas consultando la API de Open Library.
     * @param reviews lista de reseñas a enriquecer
     */
    private void enrichMissingBookData(List<Review> reviews) {
        if (reviews == null || reviews.isEmpty()) return;
        for (Review r : reviews) {
            try {
                if (r.getBookApiId() == null) continue;
                boolean missingTitle = r.getBookTitle() == null || r.getBookTitle().isBlank();
                boolean missingCover = r.getCoverUrl() == null || r.getCoverUrl().isBlank();
                if (!missingTitle && !missingCover) continue;
                String apiId = r.getBookApiId();
                String workKey = apiId;
                if (workKey.startsWith("/")) workKey = workKey.substring(1);
                if (workKey.startsWith("works/")) workKey = workKey.substring("works/".length());
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
                        r.setCoverUrl("https://covers.openlibrary.org/b/works/" + workKey + "-M.jpg");
                    }
                }
                reviewRepository.save(r);
            } catch (Exception e) {
                // Ignorar errores al enriquecer datos de libros
            }
        }
    }

    /**
     * Obtiene una reseña por su ID.
     * @param id ID de la reseña
     * @return reseña encontrada
     * @throws ReviewNotFoundException si la reseña no existe
     */
    public Review getReviewById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con id " + id));
    }

    /**
     * Crea una nueva reseña. Si la puntuación está presente, se valida que esté entre 0 y 5.
     * @param review objeto con los datos de la reseña a crear
     * @return reseña creada
      * @throws ResponseStatusException si la puntuación no está entre 0 y 5
     */
    public Review createReview(Review review) {
        if (review.getRating() != null) {
            double r = review.getRating();
            if (r < 0.0 || r > 5.0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La puntuación debe estar entre 0 y 5");
            }
        }
        return reviewRepository.save(review);
    }

    /**
    * Actualiza una reseña existente.
    * @param id ID de la reseña a actualizar
    * @param reviewDetails detalles de la reseña a actualizar
    * @return reseña actualizada
    * @throws ReviewNotFoundException si la reseña no existe
    * @throws ResponseStatusException si la puntuación no está entre 0 y 5
    */
    public Review updateReview(Long id, Review reviewDetails) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ReviewNotFoundException("Reseña no encontrada con id " + id));
        review.setComment(reviewDetails.getComment());
        // validate rating
        if (reviewDetails.getRating() != null) {
            double r = reviewDetails.getRating();
            if (r < 0.0 || r > 5.0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La puntuación debe estar entre 0 y 5");
            }
            review.setRating(reviewDetails.getRating());
        } else {
            review.setRating(reviewDetails.getRating());
        }
        review.setDate(reviewDetails.getDate());
        if (reviewDetails.getBookTitle() != null)
            review.setBookTitle(reviewDetails.getBookTitle());
        if (reviewDetails.getCoverUrl() != null)
            review.setCoverUrl(reviewDetails.getCoverUrl());
        return reviewRepository.save(review);
    }

    /**
     * Elimina una reseña por su ID.
     * @param id ID de la reseña a eliminar
     */
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
