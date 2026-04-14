package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.User;
import com.tfg.lunaris_backend.domain.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test para {@link UserController}.
 */
class UserControllerTest {

    /**
     * Verifica que los métodos del controlador delegan correctamente en el servicio.
     */
    @Test
    void basic() {
        UserService svc = mock(UserService.class);
        UserController c = new UserController();
        ReflectionTestUtils.setField(c, "userService", svc);

        User u = new User();
        u.setId(7L);
        when(svc.getAllUsers()).thenReturn(List.of(u));
        assertEquals(1, c.getAllUsers().size());

        when(svc.getUserById(7L)).thenReturn(u);
        assertEquals(u, c.getUserById(7L));

        when(svc.createUser(u)).thenReturn(u);
        assertEquals(u, c.createUser(u));

        when(svc.updateUser(7L, u)).thenReturn(u);
        assertEquals(u, c.updateUser(7L, u));

        when(svc.updateUserByUsername("name", u)).thenReturn(u);
        assertEquals(u, c.updateUserByUsername("name", u));

        c.deleteUser(7L);

        assertEquals("Hola desde LunarisBackend!", c.home());
    }
}
