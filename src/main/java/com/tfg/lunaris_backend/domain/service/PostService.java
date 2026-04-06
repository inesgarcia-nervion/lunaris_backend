package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tfg.lunaris_backend.data.repository.PostRepository;
import com.tfg.lunaris_backend.domain.model.Post;
import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con los posts.
 * 
 * Proporciona métodos para crear, obtener y eliminar posts.
 */
@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    /**
     * Obtiene todos los posts ordenados por ID de forma descendente.
     * @return lista de posts
     */
    public List<Post> getAllPosts() {
        return postRepository.findAllByOrderByIdDesc();
    }

    /**
     * Obtiene un post por su ID.
     * @param id ID del post
     * @return post encontrado o null si no existe
     */ 
    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    /**
     * Crea un nuevo post.
     * @param post post a crear
     * @return post creado
     */
    public Post createPost(Post post) {
        return postRepository.save(post);
    }

    /**
     * Elimina un post por su ID.
     * @param id ID del post a eliminar
     */
    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }
}
