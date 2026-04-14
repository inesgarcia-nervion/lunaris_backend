package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.UserRepository;
import com.tfg.lunaris_backend.domain.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para la clase CustomUserDetailsService.
 */
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository repo;

    @InjectMocks
    private CustomUserDetailsService svc;

    /**
     * Verifica que se puede cargar un usuario por nombre de usuario correctamente y que se lanza una excepción cuando no se encuentra.
     */
    @Test
    void loadUserByUsername_successAndNotFound() {
        User u = new User(); u.setUsername("u"); u.setPassword("p"); u.setRole("admin");
        when(repo.findByUsername("u")).thenReturn(Optional.of(u));
        var ud = svc.loadUserByUsername("u");
        assertEquals("u", ud.getUsername());
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().startsWith("ROLE_")));

        when(repo.findByUsername("x")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> svc.loadUserByUsername("x"));
    }

    /**
     * Verifica que se asigna el rol por defecto "USER" cuando el rol del usuario es nulo.
     */
    @Test
    void loadUserByUsername_nullRole_defaultsToUser() {
        User u = new User(); u.setUsername("u2"); u.setPassword("p2"); u.setRole(null);
        when(repo.findByUsername("u2")).thenReturn(Optional.of(u));
        var ud = svc.loadUserByUsername("u2");
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    /**
     * Verifica que se asigna el rol por defecto "USER" cuando el rol del usuario está en blanco.
     */
    @Test
    void loadUserByUsername_blankRole_defaultsToUser() {
        User u = new User(); u.setUsername("u3"); u.setPassword("p3"); u.setRole("   ");
        when(repo.findByUsername("u3")).thenReturn(Optional.of(u));
        var ud = svc.loadUserByUsername("u3");
        assertTrue(ud.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }
}
