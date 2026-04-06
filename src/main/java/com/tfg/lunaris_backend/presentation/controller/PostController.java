package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tfg.lunaris_backend.domain.model.Post;
import com.tfg.lunaris_backend.domain.service.PostService;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controlador que maneja las operaciones relacionadas con los posts.
 * 
 * Proporciona endpoints para crear, obtener y eliminar posts.
 */
@RestController
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * Endpoint para obtener todos los posts. Devuelve una lista de todos los posts disponibles.
     * @return lista de posts
     */
    @GetMapping("/posts")
    public List<Post> getAllPosts() {
        return postService.getAllPosts();
    }

    /**
     * Endpoint para obtener un post por su ID. Devuelve el post correspondiente si existe.
     * @param id identificador del post
     * @return post encontrado
     */
    @GetMapping("/posts/{id}")
    public Post getPostById(@PathVariable Long id) {
        return postService.getPostById(id);
    }

    /**
     * Endpoint para crear un nuevo post. Si la autenticación está disponible, se establece automáticamente el nombre de usuario.
     * @param post objeto con los datos del post a crear
     * @param auth información de autenticación del usuario
     * @return post creado
     */
    @PostMapping("/posts")
    public Post createPost(@RequestBody Post post, Authentication auth) {
        if (auth != null && (post.getUsername() == null || post.getUsername().isBlank())) {
            post.setUsername(auth.getName());
        }
        return postService.createPost(post);
    }

    /**
    * Endpoint para eliminar un post por su ID. Solo el autor del post o un usuario con rol ADMIN pueden eliminarlo.
    * @param id identificador del post a eliminar
    * @param auth información de autenticación del usuario
    */
    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable Long id, Authentication auth) {
        Post existing = postService.getPostById(id);
        if (existing == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado");
        }
        String currentUser = auth != null ? auth.getName() : null;
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (isAdmin || (currentUser != null && currentUser.equals(existing.getUsername()))) {
            postService.deletePost(id);
            return;
        }

        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado a eliminar este post");
    }
}
