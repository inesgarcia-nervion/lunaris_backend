package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una saga en la base de datos.
 * 
 * Contiene información sobre la saga, incluyendo su nombre y los libros que la componen.
 */
@Entity
@Table(name = "sagas", schema = "public")
@Data 
public class Saga {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @OneToMany(mappedBy = "saga", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<SagaBook> books = new ArrayList<>();
}