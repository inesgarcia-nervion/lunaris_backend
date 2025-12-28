package com.tfg.lunaris_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.model.UserList;

public interface UserListRepository extends JpaRepository<UserList, Long> {

}
