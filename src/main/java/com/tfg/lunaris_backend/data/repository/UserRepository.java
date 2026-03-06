package com.tfg.lunaris_backend.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    java.util.Optional<User> findByUsername(String username);

    java.util.Optional<User> findByEmail(String email);
}
