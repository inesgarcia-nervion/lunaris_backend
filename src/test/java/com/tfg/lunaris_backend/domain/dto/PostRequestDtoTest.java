package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase {@link PostRequestDto}.
 */
class PostRequestDtoTest {

    /**
     * Verifica que el constructor por defecto crea un objeto con todos los campos
     * nulos.
     */
    @Test
    void defaultConstructor_fieldsAreNull() {
        PostRequestDto dto = new PostRequestDto();

        assertNull(dto.getText());
        assertNull(dto.getImageUrls());
        assertNull(dto.getUserAvatarUrl());
    }

    /**
     * Verifica que se pueden establecer y obtener todos los campos del DTO.
     */
    @Test
    void settersAndGetters_workCorrectly() {
        PostRequestDto dto = new PostRequestDto();
        dto.setText("Contenido del post");
        dto.setImageUrls(List.of("img1.jpg", "img2.jpg"));
        dto.setUserAvatarUrl("https://ejemplo.com/avatar.png");

        assertEquals("Contenido del post", dto.getText());
        assertEquals(2, dto.getImageUrls().size());
        assertEquals("img1.jpg", dto.getImageUrls().get(0));
        assertEquals("https://ejemplo.com/avatar.png", dto.getUserAvatarUrl());
    }

    /**
     * Verifica que se puede establecer una lista de imágenes vacía.
     */
    @Test
    void setImageUrls_emptyList() {
        PostRequestDto dto = new PostRequestDto();
        dto.setImageUrls(List.of());

        assertNotNull(dto.getImageUrls());
        assertTrue(dto.getImageUrls().isEmpty());
    }

    /**
     * Verifica que se puede sobrescribir el texto del post.
     */
    @Test
    void setText_overwritesPreviousValue() {
        PostRequestDto dto = new PostRequestDto();
        dto.setText("Primer texto");
        dto.setText("Segundo texto");

        assertEquals("Segundo texto", dto.getText());
    }
}
