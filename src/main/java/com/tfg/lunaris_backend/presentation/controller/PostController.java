package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tfg.lunaris_backend.domain.dto.CommentRequestDto;
import com.tfg.lunaris_backend.domain.dto.PostRequestDto;
import com.tfg.lunaris_backend.domain.dto.PostResponseDto;
import com.tfg.lunaris_backend.domain.model.Comment;
import com.tfg.lunaris_backend.domain.model.Post;
import com.tfg.lunaris_backend.domain.service.PostService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Controlador que maneja las operaciones relacionadas con los posts.
 *
 * Proporciona endpoints para crear, obtener, actualizar y eliminar posts,
 * así como gestionar likes y comentarios.
 */
@RestController
public class PostController {

    @Autowired
    private PostService postService;

    private PostResponseDto toDto(Post post, String currentUser) {
        boolean liked = currentUser != null && post.getLikedByUsers() != null
                && post.getLikedByUsers().contains(currentUser);
        List<PostResponseDto.CommentDto> commentDtos = post.getComments() == null ? List.of()
                : post.getComments().stream()
                        .map(c -> new PostResponseDto.CommentDto(
                                c.getId(),
                                new PostResponseDto.UserDto(c.getUsername(), c.getUserAvatarUrl()),
                                c.getText()))
                        .collect(Collectors.toList());
        return new PostResponseDto(
                post.getId(),
                new PostResponseDto.UserDto(post.getUsername(), post.getUserAvatarUrl()),
                post.getImageUrls() == null ? List.of() : post.getImageUrls(),
                post.getContent(),
                post.getLikes(),
                liked,
                commentDtos);
    }

    /**
     * Endpoint para obtener todos los posts.
     * 
     * @param auth información de autenticación del usuario (opcional)
     * @return lista de posts en formato DTO
     */
    @GetMapping("/posts")
    public List<PostResponseDto> getAllPosts(Authentication auth) {
        String currentUser = auth != null ? auth.getName() : null;
        return postService.getAllPosts().stream()
                .map(p -> toDto(p, currentUser))
                .collect(Collectors.toList());
    }

    /**
     * Endpoint para obtener un post por su ID.
     * 
     * @param id   identificador del post
     * @param auth información de autenticación
     * @return post encontrado en formato DTO
     */
    @GetMapping("/posts/{id}")
    public PostResponseDto getPostById(@PathVariable Long id, Authentication auth) {
        Post post = postService.getPostById(id);
        if (post == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado");
        String currentUser = auth != null ? auth.getName() : null;
        return toDto(post, currentUser);
    }

    /**
     * Endpoint para crear un nuevo post.
     * 
     * @param dto  datos del nuevo post
     * @param auth información de autenticación del usuario
     * @return post creado en formato DTO
     */
    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto dto, Authentication auth) {
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Post post = new Post();
        post.setContent(dto.getText());
        post.setUsername(auth.getName());
        post.setUserAvatarUrl(dto.getUserAvatarUrl());
        post.setDate(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        if (dto.getImageUrls() != null)
            post.getImageUrls().addAll(dto.getImageUrls());
        Post saved = postService.createPost(post);
        return toDto(saved, auth.getName());
    }

    /**
     * Endpoint para actualizar un post existente.
     * 
     * @param id   identificador del post
     * @param dto  nuevos datos del post
     * @param auth información de autenticación
     * @return post actualizado en formato DTO
     */
    @PutMapping("/posts/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto dto, Authentication auth) {
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Post existing = postService.getPostById(id);
        if (existing == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado");
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin && !auth.getName().equals(existing.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }
        Post updated = postService.updatePost(id, dto.getText(), dto.getImageUrls());
        return toDto(updated, auth.getName());
    }

    /**
     * Endpoint para eliminar un post.
     * 
     * @param id   identificador del post
     * @param auth información de autenticación
     */
    @DeleteMapping("/posts/{id}")
    public void deletePost(@PathVariable Long id, Authentication auth) {
        Post existing = postService.getPostById(id);
        if (existing == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado");
        String currentUser = auth != null ? auth.getName() : null;
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (isAdmin || (currentUser != null && currentUser.equals(existing.getUsername()))) {
            postService.deletePost(id);
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado a eliminar este post");
    }

    /**
     * Endpoint para alternar el like de un post.
     * 
     * @param id   identificador del post
     * @param auth información de autenticación
     * @return post actualizado en formato DTO
     */
    @PostMapping("/posts/{id}/like")
    public PostResponseDto toggleLike(@PathVariable Long id, Authentication auth) {
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Post updated = postService.toggleLike(id, auth.getName());
        if (updated == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado");
        return toDto(updated, auth.getName());
    }

    /**
     * Endpoint para añadir un comentario a un post.
     * 
     * @param id   identificador del post
     * @param dto  datos del comentario
     * @param auth información de autenticación
     * @return post actualizado con el nuevo comentario
     */
    @PostMapping("/posts/{id}/comments")
    public PostResponseDto addComment(@PathVariable Long id, @RequestBody CommentRequestDto dto, Authentication auth) {
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Post post = postService.addComment(id, dto.getText(), auth.getName(), dto.getUserAvatarUrl());
        if (post == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado");
        return toDto(post, auth.getName());
    }

    /**
     * Endpoint para eliminar un comentario de un post.
     * 
     * @param id        identificador del post
     * @param commentId identificador del comentario
     * @param auth      información de autenticación
     * @return post actualizado sin el comentario eliminado
     */
    @DeleteMapping("/posts/{id}/comments/{commentId}")
    public PostResponseDto deleteComment(@PathVariable Long id, @PathVariable Long commentId, Authentication auth) {
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        Post post = postService.getPostById(id);
        if (post == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado");
        boolean isAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        Comment comment = post.getComments().stream().filter(c -> c.getId().equals(commentId)).findFirst().orElse(null);
        if (comment == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentario no encontrado");
        if (!isAdmin && !auth.getName().equals(comment.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado a eliminar este comentario");
        }
        Post updated = postService.deleteComment(id, commentId);
        if (updated == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post no encontrado");
        return toDto(updated, auth.getName());
    }
}
