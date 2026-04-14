package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.UserListRepository;
import com.tfg.lunaris_backend.domain.model.UserList;
import com.tfg.lunaris_backend.presentation.exceptions.UserListNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para {@link UserListService}.
 */
@ExtendWith(MockitoExtension.class)
class UserListServiceTest {

    @Mock
    private UserListRepository repo;

    @InjectMocks
    private UserListService svc;

    /**
     * Verifica los flujos principales de {@link UserListService}.
     */
    @Test
    void listFlows() {
        UserList ul = new UserList(); ul.setName("L");
        when(repo.findAll()).thenReturn(List.of(ul));
        assertFalse(svc.getAllUserLists().isEmpty());

        when(repo.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(ul)));
        Page<UserList> p = svc.getAllUserLists(Pageable.unpaged());
        assertEquals(1, p.getContent().size());

        when(repo.findById(1L)).thenReturn(Optional.of(ul));
        assertSame(ul, svc.getUserListById(1L));

        when(repo.save(ul)).thenReturn(ul);
        assertSame(ul, svc.createUserList(ul));

        UserList details = new UserList(); details.setName("New");
        when(repo.findById(2L)).thenReturn(Optional.of(ul));
        when(repo.save(ul)).thenReturn(ul);
        assertEquals("New", svc.updateUserList(2L, details).getName());

        svc.deleteUserList(3L);
        verify(repo).deleteById(3L);
    }

    /**
     * Verifica que se lanza una excepción cuando no se encuentra una lista de usuarios por ID.
     * @throws Exception si ocurre un error durante el test
     */
    @Test
    void getUserListByIdNotFoundThrows() {
        when(repo.findById(9L)).thenReturn(Optional.empty());
        assertThrows(UserListNotFoundException.class, () -> svc.getUserListById(9L));
    }

    /**
     * Verifica que se lanza una excepción cuando se intenta actualizar una lista de usuarios que no existe.
     * @throws Exception si ocurre un error durante el test
     */
    @Test
    void updateUserList_notFoundThrows() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(UserListNotFoundException.class, () -> svc.updateUserList(99L, new UserList()));
    }
}
