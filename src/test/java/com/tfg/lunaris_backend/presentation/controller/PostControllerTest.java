package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.dto.PostRequestDto;
import com.tfg.lunaris_backend.domain.dto.PostResponseDto;
import com.tfg.lunaris_backend.domain.model.Post;
import com.tfg.lunaris_backend.domain.service.PostService;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para {@link PostController}.
 */
class PostControllerTest {

    private PostController buildController(PostService svc) {
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);
        return c;
    }

    /**
     * Verifica que getAllPosts delega en el servicio y devuelve DTOs.
     */
    @Test
    void getAllPosts_returnsDtos() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);

        Post p = new Post();
        p.setId(4L);
        p.setUsername("owner");
        p.setContent("hello");

        when(svc.getAllPosts()).thenReturn(List.of(p));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("owner");

        List<PostResponseDto> result = c.getAllPosts(auth);
        assertEquals(1, result.size());
        assertEquals(4L, result.get(0).getId());
        assertEquals("hello", result.get(0).getText());
    }

    /**
     * Verifica que getPostById devuelve el DTO correctamente.
     */
    @Test
    void getPostById_returnsDto() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);

        Post p = new Post();
        p.setId(4L);
        p.setUsername("owner");
        p.setContent("hello");

        when(svc.getPostById(4L)).thenReturn(p);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("owner");

        PostResponseDto dto = c.getPostById(4L, auth);
        assertEquals(4L, dto.getId());
    }

    /**
     * Verifica que getPostById lanza NOT_FOUND cuando no existe el post.
     */
    @Test
    void getPostById_notFound_throwsNotFound() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);

        when(svc.getPostById(99L)).thenReturn(null);

        Authentication auth = mock(Authentication.class);
        var ex = assertThrows(ResponseStatusException.class, () -> c.getPostById(99L, auth));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    /**
     * Verifica que createPost crea el post con el username de la autenticación.
     */
    @Test
    void createPost_setsUsernameFromAuth() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);

        PostRequestDto dto = new PostRequestDto();
        dto.setText("hello");
        dto.setImageUrls(List.of());

        Post saved = new Post();
        saved.setId(1L);
        saved.setContent("hello");
        saved.setUsername("authuser");

        when(svc.createPost(any())).thenReturn(saved);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("authuser");

        PostResponseDto result = c.createPost(dto, auth);
        assertEquals("authuser", result.getUser().getName());
    }

    /**
     * Verifica que createPost lanza UNAUTHORIZED si auth es nulo.
     */
    @Test
    void createPost_authNull_throwsUnauthorized() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);
        PostRequestDto dto = new PostRequestDto();

        var ex = assertThrows(ResponseStatusException.class, () -> c.createPost(dto, null));
        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
    }

    /**
     * Verifica que un administrador puede eliminar un post.
     */
    @Test
    void deletePost_asAdmin_succeeds() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);

        Post p = new Post();
        p.setId(5L);
        p.setUsername("someone");
        when(svc.getPostById(5L)).thenReturn(p);

        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))).when(auth).getAuthorities();
        when(auth.getName()).thenReturn("admin");

        c.deletePost(5L, auth);
        verify(svc).deletePost(5L);
    }

    /**
     * Verifica que un usuario diferente no puede eliminar un post.
     */
    @Test
    void deletePost_differentUser_throwsForbidden() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);

        Post p = new Post();
        p.setId(6L);
        p.setUsername("owner");
        when(svc.getPostById(6L)).thenReturn(p);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("notowner");
        when(auth.getAuthorities()).thenReturn(Collections.emptyList());

        var ex = assertThrows(ResponseStatusException.class, () -> c.deletePost(6L, auth));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    /**
     * Verifica que la eliminación de un post falla si la autenticación es nula.
     */
    @Test
    void deletePost_authNull_throwsForbidden() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);

        Post p = new Post();
        p.setId(7L);
        p.setUsername("owner");
        when(svc.getPostById(7L)).thenReturn(p);

        var ex = assertThrows(ResponseStatusException.class, () -> c.deletePost(7L, null));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    /**
     * Verifica que la eliminación de un post falla si el post no existe.
     */
    @Test
    void deletePost_existingNull_throwsNotFound() {
        PostService svc = mock(PostService.class);
        PostController c = buildController(svc);

        when(svc.getPostById(8L)).thenReturn(null);

        Authentication auth = mock(Authentication.class);
        var ex = assertThrows(ResponseStatusException.class, () -> c.deletePost(8L, auth));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}