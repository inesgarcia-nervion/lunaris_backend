package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase Genre.
 */
class GenreTest {

    /**
     * Verifica que se pueden establecer y obtener las propiedades de la clase.
     */
    @Test
    void nameProperty() {
        Genre g = new Genre();
        g.setName("Fantasy");
        assertEquals("Fantasy", g.getName());
    }
}
