package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SagaTest {

    @Test
    void booksListManipulation() {
        Saga s = new Saga();
        s.setName("Saga X");

        SagaBook sb = new SagaBook();
        sb.setTitle("Part 1");
        sb.setAuthor("Author");

        // associate
        sb.setSaga(s);
        s.getBooks().add(sb);

        assertEquals("Saga X", s.getName());
        assertFalse(s.getBooks().isEmpty());
        assertEquals("Part 1", s.getBooks().get(0).getTitle());
        assertSame(s, s.getBooks().get(0).getSaga());
    }
}
