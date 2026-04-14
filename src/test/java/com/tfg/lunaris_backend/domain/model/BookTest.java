package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase Book.
 */
class BookTest {
    /**
    * Verifica que se pueden establecer y obtener las propiedades de la clase, incluyendo los géneros.
    */
    @Test
    void gettersAndSettersAndGenres() {
        Book b = new Book();
        b.setTitle("Title");
        b.setAuthor("Author");
        b.setApiId("API123");
        b.setReleaseYear(2020);
        b.setScore(4.5);

        Genre g = new Genre();
        g.setName("Fiction");

        b.getGenres().add(g);

        assertEquals("Title", b.getTitle());
        assertEquals("Author", b.getAuthor());
        assertEquals("API123", b.getApiId());
        assertEquals(2020, b.getReleaseYear());
        assertEquals(4.5, b.getScore());
        assertFalse(b.getGenres().isEmpty());
        assertEquals("Fiction", b.getGenres().get(0).getName());
    }
}
