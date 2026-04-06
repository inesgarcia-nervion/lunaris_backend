package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.User;

/**
 * Repositorio JPA para la entidad `User`.
 *
 * Proporciona operaciones CRUD y consultas específicas para usuarios.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username nombre de usuario a buscar
     * @return optional con el usuario si existe, vacío en caso contrario
     */
    java.util.Optional<User> findByUsername(String username);

    /**
     * Busca un usuario por su correo electrónico.
     *
     * @param email correo electrónico a buscar
     * @return optional con el usuario si existe, vacío en caso contrario
     */
    java.util.Optional<User> findByEmail(String email);
}
