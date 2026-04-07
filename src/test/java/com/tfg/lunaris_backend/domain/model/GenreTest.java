package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void nameProperty() {
        Genre g = new Genre();
        g.setName("Fantasy");
        assertEquals("Fantasy", g.getName());
    }
}
