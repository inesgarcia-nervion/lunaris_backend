package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase BookRequest.
 */
class BookRequestTest {

    /**
     * Verifica que se pueden establecer y obtener las propiedades de la clase.
     */
    @Test
    void constructorAndSetters() {
        BookRequest br = new BookRequest("T","A");
        assertEquals("T", br.getTitle());
        assertEquals("A", br.getAuthor());

        br.setTitle("NewT");
        br.setAuthor("NewA");
        br.setId(5L);
        assertEquals(5L, br.getId());
        assertEquals("NewT", br.getTitle());
        assertEquals("NewA", br.getAuthor());
    }
}
