package com.tfg.lunaris_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.tfg.lunaris_backend.model.Saga;

public interface SagaRepository extends JpaRepository<Saga, Long> {

}
