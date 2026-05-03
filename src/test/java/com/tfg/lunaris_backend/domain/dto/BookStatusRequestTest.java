package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase {@link BookStatusRequest}.
 */
class BookStatusRequestTest {

    /**
     * Verifica que el constructor por defecto crea un objeto con todos los campos
     * nulos.
     */
    @Test
    void defaultConstructor_fieldsAreNull() {
        BookStatusRequest req = new BookStatusRequest();

        assertNull(req.getBookId());
        assertNull(req.getStatus());
        assertNull(req.getBookData());
    }

    /**
     * Verifica que se pueden establecer y obtener el bookId y el status.
     */
    @Test
    void settersAndGetters_workCorrectly() {
        BookStatusRequest req = new BookStatusRequest();
        req.setBookId("/works/OL12345W");
        req.setStatus("Leyendo");

        assertEquals("/works/OL12345W", req.getBookId());
        assertEquals("Leyendo", req.getStatus());
    }

    /**
     * Verifica que se puede establecer y obtener el campo bookData con un objeto
     * arbitrario.
     */
    @Test
    void setBookData_withObject() {
        BookStatusRequest req = new BookStatusRequest();
        Object datos = java.util.Map.of("title", "El Quijote", "author", "Cervantes");
        req.setBookData(datos);

        assertNotNull(req.getBookData());
        assertSame(datos, req.getBookData());
    }

    /**
     * Verifica todos los posibles valores de status.
     */
    @Test
    void setStatus_allValidValues() {
        BookStatusRequest req = new BookStatusRequest();

        req.setStatus("Plan para leer");
        assertEquals("Plan para leer", req.getStatus());

        req.setStatus("Leyendo");
        assertEquals("Leyendo", req.getStatus());

        req.setStatus("Leído");
        assertEquals("Leído", req.getStatus());

        req.setStatus(null);
        assertNull(req.getStatus());
    }
}
