package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {

    @Test
    void simpleProperties() {
        Review r = new Review();
        r.setComment("Nice book");
        r.setRating(4.0);
        r.setDate("2020-01-01");
        r.setBookApiId("B1");
        r.setBookTitle("Title");
        r.setCoverUrl("url");
        r.setUsername("user1");

        assertEquals("Nice book", r.getComment());
        assertEquals(4.0, r.getRating());
        assertEquals("2020-01-01", r.getDate());
        assertEquals("B1", r.getBookApiId());
        assertEquals("Title", r.getBookTitle());
        assertEquals("url", r.getCoverUrl());
        assertEquals("user1", r.getUsername());
    }
}
