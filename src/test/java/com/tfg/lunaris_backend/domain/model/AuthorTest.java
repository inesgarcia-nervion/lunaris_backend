package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase Author.
 */
class AuthorTest {

    /**
     * Verifica que se pueden establecer y obtener las propiedades de la clase.
     */
    @Test
    void settersAndGetters() {
        Author a = new Author();
        a.setName("An Author");
        a.setBooks("Book1, Book2");

        assertEquals("An Author", a.getName());
        assertEquals("Book1, Book2", a.getBooks());
    }
}
