package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.UserList;
import com.tfg.lunaris_backend.domain.service.UserListService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test para {@link UserListController}.
 */
class UserListControllerTest {

    /**
     * Verifica que los métodos del controlador delegan correctamente en el servicio.
     */
    @Test
    void delegates() {
        UserListService svc = mock(UserListService.class);
        UserListController c = new UserListController();
        ReflectionTestUtils.setField(c, "userListService", svc);

        UserList ul = new UserList();
        ul.setId(8L);

        when(svc.getAllUserLists(PageRequest.of(0,10))).thenReturn(new PageImpl<>(List.of(ul)));
        assertEquals(1, c.getAllUserLists(PageRequest.of(0,10)).getTotalElements());

        when(svc.getUserListById(8L)).thenReturn(ul);
        assertEquals(ul, c.getUserListById(8L));

        when(svc.createUserList(ul)).thenReturn(ul);
        assertEquals(ul, c.createUserList(ul));

        when(svc.updateUserList(8L, ul)).thenReturn(ul);
        assertEquals(ul, c.updateUserList(8L, ul));

        c.deleteUserList(8L);
    }
}
