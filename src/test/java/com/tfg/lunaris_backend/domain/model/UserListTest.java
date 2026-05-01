package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase UserList.
 */
class UserListTest {

    /**
     * Verifica que se puede establecer y obtener el nombre de la lista.
     */
    @Test
    void nameProperty() {
        UserList ul = new UserList();
        ul.setName("MyList");
        assertEquals("MyList", ul.getName());
    }
}
