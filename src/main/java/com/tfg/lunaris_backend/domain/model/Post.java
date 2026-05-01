package com.tfg.lunaris_backend.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entidad que representa una publicación en la base de datos.
 *
 * Contiene información sobre la publicación, incluyendo su contenido, el nombre
 * de usuario del autor,
 * la fecha de publicación, imágenes, likes y comentarios.
 */
@Entity
@Table(name = "posts", schema = "public")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private String username;

    private String userAvatarUrl;

    private String date;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_images", schema = "public", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "image_url", columnDefinition = "TEXT")
    @OrderColumn(name = "image_order")
    private List<String> imageUrls = new ArrayList<>();

    private int likes = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "post_likes", schema = "public", joinColumns = @JoinColumn(name = "post_id"))
    @Column(name = "username")
    private Set<String> likedByUsers = new HashSet<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<Comment> comments = new ArrayList<>();
}
