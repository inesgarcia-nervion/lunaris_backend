package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.PostRepository;
import com.tfg.lunaris_backend.domain.model.Post;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para la clase PostService.
 */
@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository repo;

    @InjectMocks
    private PostService svc;

    /**
     * Verifica los flujos principales de la clase PostService, incluyendo la obtención, creación y eliminación de publicaciones.
     */
    @Test
    void flows() {
        Post p = new Post(); p.setContent("c");
        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(p));
        assertFalse(svc.getAllPosts().isEmpty());

        when(repo.findById(1L)).thenReturn(Optional.of(p));
        assertNotNull(svc.getPostById(1L));

        when(repo.save(p)).thenReturn(p);
        assertSame(p, svc.createPost(p));

        svc.deletePost(2L);
        verify(repo).deleteById(2L);
    }
}
