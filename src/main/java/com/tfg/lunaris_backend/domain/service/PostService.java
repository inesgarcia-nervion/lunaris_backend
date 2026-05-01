package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tfg.lunaris_backend.data.repository.CommentRepository;
import com.tfg.lunaris_backend.data.repository.PostRepository;
import com.tfg.lunaris_backend.domain.model.Comment;
import com.tfg.lunaris_backend.domain.model.Post;
import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con los posts.
 *
 * Proporciona métodos para crear, obtener, actualizar y eliminar posts,
 * así como gestionar likes y comentarios.
 */
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    /**
     * Obtiene todos los posts ordenados por ID de forma descendente.
     * 
     * @return lista de posts
     */
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByIdDesc();
    }

    /**
     * Obtiene un post por su ID.
     * 
     * @param id ID del post
     * @return post encontrado o null si no existe
     */
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    /**
     * Crea un nuevo post.
     * 
     * @param post post a crear
     * @return post creado
     */
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    /**
     * Actualiza el contenido e imágenes de un post existente.
     * 
     * @param id        ID del post a actualizar
     * @param text      nuevo texto del post
     * @param imageUrls nuevas imágenes del post
     * @return post actualizado o null si no existe
     */
    public Post updatePost(Long id, String text, List<String> imageUrls) {
        Post existing = postRepository.findById(id).orElse(null);
        if (existing == null)
            return null;
        existing.setContent(text);
        existing.getImageUrls().clear();
        if (imageUrls != null)
            existing.getImageUrls().addAll(imageUrls);
        return postRepository.save(existing);
    }

    /**
     * Alterna el like de un usuario en un post.
     * 
     * @param id       ID del post
     * @param username nombre del usuario
     * @return post actualizado o null si no existe
     */
    public Post toggleLike(Long id, String username) {
        Post existing = postRepository.findById(id).orElse(null);
        if (existing == null)
            return null;
        if (existing.getLikedByUsers().contains(username)) {
            existing.getLikedByUsers().remove(username);
            existing.setLikes(Math.max(0, existing.getLikes() - 1));
        } else {
            existing.getLikedByUsers().add(username);
            existing.setLikes(existing.getLikes() + 1);
        }
        return postRepository.save(existing);
    }

    /**
     * Añade un comentario a un post.
     * 
     * @param postId        ID del post
     * @param text          texto del comentario
     * @param username      autor del comentario
     * @param userAvatarUrl avatar del autor
     * @return comentario creado o null si el post no existe
     */
    public Post addComment(Long postId, String text, String username, String userAvatarUrl) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null)
            return null;
        Comment comment = new Comment();
        comment.setText(text);
        comment.setUsername(username);
        comment.setUserAvatarUrl(userAvatarUrl);
        comment.setPost(post);
        post.getComments().add(comment);
        return postRepository.save(post);
    }

    /**
     * Elimina un comentario de un post y devuelve el post actualizado.
     * 
     * @param postId    ID del post
     * @param commentId ID del comentario
     * @return post actualizado o null si no existe
     */
    public Post deleteComment(Long postId, Long commentId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null)
            return null;
        post.getComments().removeIf(c -> c.getId().equals(commentId));
        return postRepository.save(post);
    }

    /**
     * Elimina un post por su ID.
     * 
     * @param id ID del post a eliminar
     */
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
