package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.UserList;
import com.tfg.lunaris_backend.domain.service.UserListService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test para {@link UserListController}.
 */
class UserListControllerTest {

    private Authentication ownerAuth(String username) {
        return new Authentication() {
            @Override
            public String getName() {
                return username;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return List.of();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return username;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean b) {
            }
        };
    }

    /**
     * Verifica que los métodos del controlador delegan correctamente en el
     * servicio.
     */
    @Test
    void delegates() {
        UserListService svc = mock(UserListService.class);
        UserListController c = new UserListController();
        ReflectionTestUtils.setField(c, "userListService", svc);

        UserList ul = new UserList();
        ul.setId(8L);
        ul.setOwner("alice");

        Authentication auth = ownerAuth("alice");

        when(svc.getAllUserLists(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(List.of(ul)));
        assertEquals(1, c.getAllUserLists(PageRequest.of(0, 10)).getTotalElements());

        when(svc.getUserListById(8L)).thenReturn(ul);
        assertEquals(ul, c.getUserListById(8L));

        when(svc.createUserList(ul)).thenReturn(ul);
        assertEquals(ul, c.createUserList(ul));

        when(svc.getUserListById(8L)).thenReturn(ul);
        when(svc.updateUserList(8L, ul)).thenReturn(ul);
        assertEquals(ul, c.updateUserList(8L, ul, auth));

        when(svc.getUserListById(8L)).thenReturn(ul);
        c.deleteUserList(8L, auth);
    }

    /**
     * Verifica que updateUserList lanza forbidden cuando el usuario no es el
     * propietario.
     */
    @Test
    void updateForbiddenForNonOwner() {
        UserListService svc = mock(UserListService.class);
        UserListController c = new UserListController();
        ReflectionTestUtils.setField(c, "userListService", svc);

        UserList ul = new UserList();
        ul.setId(1L);
        ul.setOwner("alice");

        when(svc.getUserListById(1L)).thenReturn(ul);

        Authentication other = ownerAuth("bob");
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> c.updateUserList(1L, ul, other));
    }

    /**
     * Verifica que deleteUserList lanza forbidden cuando el usuario no es el
     * propietario.
     */
    @Test
    void deleteForbiddenForNonOwner() {
        UserListService svc = mock(UserListService.class);
        UserListController c = new UserListController();
        ReflectionTestUtils.setField(c, "userListService", svc);

        UserList ul = new UserList();
        ul.setId(2L);
        ul.setOwner("alice");

        when(svc.getUserListById(2L)).thenReturn(ul);

        Authentication other = ownerAuth("bob");
        assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> c.deleteUserList(2L, other));
    }
}
