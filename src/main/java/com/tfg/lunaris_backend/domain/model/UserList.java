package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entidad que representa una lista de usuario en la base de datos.
 * 
 * Contiene información sobre la lista, incluyendo su nombre.
 */
@Entity
@Table(name = "user_list", schema = "public")
@Data 
public class UserList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
}
