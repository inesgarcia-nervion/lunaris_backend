package com.tfg.lunaris_backend.data.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tfg.lunaris_backend.domain.model.UserList;

/**
 * Repositorio JPA para `UserList`.
 *
 * Gestiona las operaciones persistentes sobre listas de usuario.
 */
public interface UserListRepository extends JpaRepository<UserList, Long> {
    List<UserList> findByOwner(String owner);
}
