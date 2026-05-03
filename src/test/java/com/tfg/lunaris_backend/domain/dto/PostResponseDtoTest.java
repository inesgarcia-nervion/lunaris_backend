package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para la clase {@link PostResponseDto} y sus clases internas
 * {@link PostResponseDto.UserDto}
 * y {@link PostResponseDto.CommentDto}.
 */
class PostResponseDtoTest {

    /**
     * Verifica que el constructor por defecto crea un objeto con todos los campos
     * en sus valores por defecto.
     */
    @Test
    void defaultConstructor_fieldsAreDefault() {
        PostResponseDto dto = new PostResponseDto();

        assertNull(dto.getId());
        assertNull(dto.getUser());
        assertNull(dto.getImageUrls());
        assertNull(dto.getText());
        assertEquals(0, dto.getLikes());
        assertFalse(dto.isLiked());
        assertNull(dto.getComments());
    }

    /**
     * Verifica que el constructor con todos los argumentos asigna correctamente
     * cada campo.
     */
    @Test
    void allArgsConstructor_assignsAllFields() {
        PostResponseDto.UserDto user = new PostResponseDto.UserDto("usuario", "avatar.jpg");
        PostResponseDto.CommentDto comment = new PostResponseDto.CommentDto(10L, user, "Comentario");
        List<String> images = List.of("img1.jpg");

        PostResponseDto dto = new PostResponseDto(1L, user, images, "Texto del post", 5, true, List.of(comment));

        assertEquals(1L, dto.getId());
        assertSame(user, dto.getUser());
        assertEquals(1, dto.getImageUrls().size());
        assertEquals("Texto del post", dto.getText());
        assertEquals(5, dto.getLikes());
        assertTrue(dto.isLiked());
        assertEquals(1, dto.getComments().size());
    }

    /**
     * Verifica que el constructor por defecto de {@link PostResponseDto.UserDto}
     * crea un objeto con campos nulos.
     */
    @Test
    void userDto_defaultConstructor_fieldsAreNull() {
        PostResponseDto.UserDto user = new PostResponseDto.UserDto();

        assertNull(user.getName());
        assertNull(user.getAvatarUrl());
    }

    /**
     * Verifica que el constructor con argumentos de {@link PostResponseDto.UserDto}
     * asigna los campos correctamente.
     */
    @Test
    void userDto_allArgsConstructor_assignsFields() {
        PostResponseDto.UserDto user = new PostResponseDto.UserDto("nombre_usuario", "https://ejemplo.com/avatar.png");

        assertEquals("nombre_usuario", user.getName());
        assertEquals("https://ejemplo.com/avatar.png", user.getAvatarUrl());
    }

    /**
     * Verifica que el constructor por defecto de {@link PostResponseDto.CommentDto}
     * crea un objeto con campos nulos o cero.
     */
    @Test
    void commentDto_defaultConstructor_fieldsAreNull() {
        PostResponseDto.CommentDto comment = new PostResponseDto.CommentDto();

        assertNull(comment.getId());
        assertNull(comment.getUser());
        assertNull(comment.getText());
    }

    /**
     * Verifica que el constructor con argumentos de
     * {@link PostResponseDto.CommentDto} asigna los campos correctamente.
     */
    @Test
    void commentDto_allArgsConstructor_assignsFields() {
        PostResponseDto.UserDto user = new PostResponseDto.UserDto("autor", "avatar.jpg");
        PostResponseDto.CommentDto comment = new PostResponseDto.CommentDto(42L, user, "Texto del comentario");

        assertEquals(42L, comment.getId());
        assertSame(user, comment.getUser());
        assertEquals("Texto del comentario", comment.getText());
    }

    /**
     * Verifica que un PostResponseDto con liked=false devuelve false en isLiked.
     */
    @Test
    void allArgsConstructor_notLiked() {
        PostResponseDto dto = new PostResponseDto(2L, null, List.of(), "Post", 0, false, List.of());

        assertFalse(dto.isLiked());
        assertEquals(0, dto.getLikes());
    }
}
