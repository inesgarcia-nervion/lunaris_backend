package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.Post;
import com.tfg.lunaris_backend.domain.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostControllerTest {

    @Test
    void createAndDeleteAndGet() {
        PostService svc = mock(PostService.class);
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);

        Post p = new Post();
        p.setId(4L);
        p.setUsername("owner");

        when(svc.getAllPosts()).thenReturn(List.of(p));
        assertEquals(1, c.getAllPosts().size());

        when(svc.getPostById(4L)).thenReturn(p);
        assertEquals(p, c.getPostById(4L));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("owner");

        when(svc.createPost(p)).thenReturn(p);
        assertEquals(p, c.createPost(p, auth));

        // delete as owner
        when(svc.getPostById(4L)).thenReturn(p);
        when(auth.getAuthorities()).thenReturn(java.util.Collections.emptyList());
        c.deletePost(4L, auth);
        verify(svc).deletePost(4L);
    }

    @Test
    void createPost_authNull_doesNotSetUsername() {
        PostService svc = mock(PostService.class);
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);

        Post p = new Post();
        p.setUsername("existing");
        when(svc.createPost(p)).thenReturn(p);

        c.createPost(p, null); // auth is null → username not overwritten
        assertEquals("existing", p.getUsername());
    }

    @Test
    void createPost_postAlreadyHasUsername_notOverwritten() {
        PostService svc = mock(PostService.class);
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);

        Post p = new Post();
        p.setUsername("myuser"); // already set → not overwritten by auth

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("other");
        when(svc.createPost(p)).thenReturn(p);

        c.createPost(p, auth);
        assertEquals("myuser", p.getUsername());
    }

    @Test
    void deletePost_asAdmin_succeeds() {
        PostService svc = mock(PostService.class);
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);

        Post p = new Post(); p.setId(5L); p.setUsername("someone");
        when(svc.getPostById(5L)).thenReturn(p);

        Authentication auth = mock(Authentication.class);
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(auth).getAuthorities();
        when(auth.getName()).thenReturn("admin");

        c.deletePost(5L, auth);
        verify(svc).deletePost(5L);
    }

    @Test
    void deletePost_differentUser_throwsForbidden() {
        PostService svc = mock(PostService.class);
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);

        Post p = new Post(); p.setId(6L); p.setUsername("owner");
        when(svc.getPostById(6L)).thenReturn(p);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("notowner");
        when(auth.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> c.deletePost(6L, auth));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void deletePost_authNull_throwsForbidden() {
        PostService svc = mock(PostService.class);
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);

        Post p = new Post(); p.setId(7L); p.setUsername("owner");
        when(svc.getPostById(7L)).thenReturn(p);

        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> c.deletePost(7L, null));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void deletePost_existingNull_throwsNotFound() {
        PostService svc = mock(PostService.class);
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);

        when(svc.getPostById(8L)).thenReturn(null);

        Authentication auth = mock(Authentication.class);
        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> c.deletePost(8L, auth));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void createPost_nullUsername_authSetsIt() {
        PostService svc = mock(PostService.class);
        PostController c = new PostController();
        ReflectionTestUtils.setField(c, "postService", svc);

        Post p = new Post();
        p.setUsername(null); // null username → auth.getName() should be used

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("authuser");
        when(svc.createPost(p)).thenReturn(p);

        c.createPost(p, auth);
        assertEquals("authuser", p.getUsername()); // covers line 56
    }
}
