package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase {@link NewsRequestDto}.
 */
class NewsRequestDtoTest {

    /**
     * Verifica que el constructor por defecto crea un objeto con todos los campos
     * nulos.
     */
    @Test
    void defaultConstructor_fieldsAreNull() {
        NewsRequestDto dto = new NewsRequestDto();

        assertNull(dto.getTitle());
        assertNull(dto.getText());
        assertNull(dto.getBody());
        assertNull(dto.getImage());
    }

    /**
     * Verifica que se pueden establecer y obtener todos los campos del DTO.
     */
    @Test
    void settersAndGetters_workCorrectly() {
        NewsRequestDto dto = new NewsRequestDto();
        dto.setTitle("Título de la noticia");
        dto.setText("Texto breve de la noticia");
        dto.setBody("Cuerpo completo de la noticia con más detalle");
        dto.setImage("https://ejemplo.com/imagen.jpg");

        assertEquals("Título de la noticia", dto.getTitle());
        assertEquals("Texto breve de la noticia", dto.getText());
        assertEquals("Cuerpo completo de la noticia con más detalle", dto.getBody());
        assertEquals("https://ejemplo.com/imagen.jpg", dto.getImage());
    }

    /**
     * Verifica que se puede sobrescribir el valor de un campo con un nuevo valor.
     */
    @Test
    void setters_overwritePreviousValue() {
        NewsRequestDto dto = new NewsRequestDto();
        dto.setTitle("Primer título");
        dto.setTitle("Segundo título");

        assertEquals("Segundo título", dto.getTitle());
    }
}
