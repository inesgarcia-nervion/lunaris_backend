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

/**
 * Controlador que maneja las operaciones relacionadas con las reseñas.
 * 
 * Proporciona endpoints para crear, obtener, actualizar y eliminar reseñas.
 */
@RestController
public class ReviewController {
    @Autowired
    private ReviewService reviewService;

    /**
     * Endpoint para obtener todas las reseñas. Devuelve una lista de todas las reseñas disponibles.
     * @return lista de reseñas
     */
    @GetMapping("/reviews")
    public List<Review> getAllReviews() {
        return reviewService.getAllReviews();
    }

    /**
     * Endpoint para obtener una reseña por su ID. Devuelve la reseña correspondiente si existe.
     * @param id identificador de la reseña
     * @return reseña encontrada
     */
    @GetMapping("/reviews/{id}")
    public Review getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id);
    }

    /**
     * Endpoint para obtener todas las reseñas asociadas a un identificador de libro externo. 
     * Devuelve una lista de reseñas para el libro.
     * @param apiId identificador externo del libro
     * @return lista de reseñas para el libro
     */
    @GetMapping("/reviews/book")
    public java.util.List<Review> getReviewsByBookApiId(@org.springframework.web.bind.annotation.RequestParam("apiId") String apiId) {
        return reviewService.getReviewsByBookApiId(apiId);
    }

    /**
     * Endpoint para crear una nueva reseña. Si la autenticación está disponible, se establece 
     * automáticamente el nombre de usuario.
     * @param review objeto con los datos de la reseña a crear
      * @param auth información de autenticación del usuario
     * @return reseña creada
     */
    @PostMapping("/reviews")
    public Review createReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    /**
     * Endpoint para actualizar una reseña existente. Solo el autor de la reseña o un usuario 
     * con rol ADMIN pueden actualizarla.
     * @param id identificador de la reseña a actualizar
     * @param reviewDetails detalles de la reseña a actualizar
     * @return reseña actualizada
     */
    @PutMapping("/reviews/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review reviewDetails) {
        return reviewService.updateReview(id, reviewDetails);
    }

    /**
     * Endpoint para eliminar una reseña por su ID. Solo el autor de la reseña o un usuario con rol ADMIN pueden eliminarla.
      * @param id identificador de la reseña a eliminar
      * @param auth información de autenticación del usuario
      * @throws ResponseStatusException si el usuario no está autorizado a eliminar la reseña
      */
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
