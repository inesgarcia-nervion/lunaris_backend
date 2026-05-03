package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase {@link CommentRequestDto}.
 */
class CommentRequestDtoTest {

    /**
     * Verifica que el constructor por defecto crea un objeto con todos los campos
     * nulos.
     */
    @Test
    void defaultConstructor_fieldsAreNull() {
        CommentRequestDto dto = new CommentRequestDto();

        assertNull(dto.getText());
        assertNull(dto.getUserAvatarUrl());
    }

    /**
     * Verifica que se pueden establecer y obtener el texto y la URL del avatar.
     */
    @Test
    void settersAndGetters_workCorrectly() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Este es un comentario de prueba");
        dto.setUserAvatarUrl("https://ejemplo.com/avatar.png");

        assertEquals("Este es un comentario de prueba", dto.getText());
        assertEquals("https://ejemplo.com/avatar.png", dto.getUserAvatarUrl());
    }

    /**
     * Verifica que se puede sobrescribir el valor del texto con un nuevo valor.
     */
    @Test
    void setText_overwritesPreviousValue() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setText("Primer comentario");
        dto.setText("Segundo comentario");

        assertEquals("Segundo comentario", dto.getText());
    }

    /**
     * Verifica que se puede establecer la URL del avatar como nulo.
     */
    @Test
    void setUserAvatarUrl_toNull() {
        CommentRequestDto dto = new CommentRequestDto();
        dto.setUserAvatarUrl("https://ejemplo.com/avatar.png");
        dto.setUserAvatarUrl(null);

        assertNull(dto.getUserAvatarUrl());
    }
}
