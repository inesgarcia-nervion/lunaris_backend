package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.Review;

/**
 * Repositorio JPA para `Review`.
 *
 * Permite recuperar reseñas por identificador de libro y consultar todas ordenadas.
 */
public interface ReviewRepository extends JpaRepository<Review, Long> {

	/**
	 * Obtiene todas las reseñas asociadas a un identificador de libro externo.
	 *
	 * @param bookApiId identificador externo del libro
	 * @return lista de reseñas para el libro
	 */
	java.util.List<Review> findByBookApiId(String bookApiId);

	/**
	 * Recupera todas las reseñas ordenadas por id de forma descendente.
	 *
	 * @return lista de reseñas ordenada por id desc
	 */
	java.util.List<Review> findAllByOrderByIdDesc();

}
