package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa un género en la base de datos.
 * 
 * Contiene información sobre el género, incluyendo su nombre.
 */
@Entity
@Table(name = "genres", schema = "public")
@Data 
public class Genre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
