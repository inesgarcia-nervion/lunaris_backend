package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "reviews", schema = "public")
@Data // Lombok genera getters, setters, toString, equals, hashCode, etc.
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    private Double rating; // allow decimals (one decimal)
    private String date;
    // Link review to a book via its api id (OpenLibrary key or custom-... id)
    private String bookApiId;
    // Optionally store book title so menu can display it without extra API calls
    private String bookTitle;
    // Store pre-resolved cover URL so menu can display it without extra API calls
    private String coverUrl;
    // Optionally store reviewer username
    private String username;
}