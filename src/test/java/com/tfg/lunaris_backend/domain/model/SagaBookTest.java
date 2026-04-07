package com.tfg.lunaris_backend.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SagaBookTest {

    @Test
    void propertiesAndSagaReference() {
        Saga s = new Saga();
        s.setName("Saga Y");

        SagaBook sb = new SagaBook();
        sb.setTitle("Entry");
        sb.setAuthor("Auth");
        sb.setOrderNumber("1");
        sb.setPages(300);
        sb.setYear(1999);
        sb.setGoodreadsUrl("http://goodreads");
        sb.setSaga(s);

        assertEquals("Entry", sb.getTitle());
        assertEquals("Auth", sb.getAuthor());
        assertEquals("1", sb.getOrderNumber());
        assertEquals(300, sb.getPages());
        assertEquals(1999, sb.getYear());
        assertEquals("http://goodreads", sb.getGoodreadsUrl());
        assertSame(s, sb.getSaga());
    }
}
