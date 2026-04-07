package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PostTest {

    @Test
    void fields() {
        Post p = new Post();
        p.setContent("c");
        p.setUsername("u");
        p.setDate("d");
        p.setImageUrl("i");

        assertEquals("c", p.getContent());
        assertEquals("u", p.getUsername());
        assertEquals("d", p.getDate());
        assertEquals("i", p.getImageUrl());
    }
}
