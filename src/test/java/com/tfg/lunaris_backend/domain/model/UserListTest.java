package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UserListTest {

    @Test
    void nameProperty() {
        UserList ul = new UserList();
        ul.setName("MyList");
        assertEquals("MyList", ul.getName());
    }
}
