package com.tfg.lunaris_backend.domain.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class SagaScrapedDtoTest {

    @Test
    void innerEntryAndContainer() {
        SagaScrapedDto.SagaBookEntry e = new SagaScrapedDto.SagaBookEntry("T","A","1",100,200,"url");
        assertEquals("T", e.getTitle());
        assertEquals("A", e.getAuthor());
        assertEquals("1", e.getOrderNumber());
        assertEquals(100, e.getPages());
        assertEquals(200, e.getYear());
        assertEquals("url", e.getStorygraphUrl());

        SagaScrapedDto dto = new SagaScrapedDto("SagaName", List.of(e));
        assertEquals("SagaName", dto.getSagaName());
        assertEquals(1, dto.getBooks().size());
    }

    @Test
    void noArgsConstructors() {
        SagaScrapedDto dto = new SagaScrapedDto();
        assertNull(dto.getSagaName());
        assertNull(dto.getBooks());

        SagaScrapedDto.SagaBookEntry entry = new SagaScrapedDto.SagaBookEntry();
        assertNull(entry.getTitle());
        entry.setTitle("T2");
        assertEquals("T2", entry.getTitle());
    }
}
