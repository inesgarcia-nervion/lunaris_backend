package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.dto.BookCreateRequest;
import com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto;
import com.tfg.lunaris_backend.domain.model.Book;
import com.tfg.lunaris_backend.domain.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BookControllerTest {

    @Test
    void basicDelegations() {
        BookService svc = mock(BookService.class);
        BookController c = new BookController();
        ReflectionTestUtils.setField(c, "bookService", svc);

        Book b = new Book();
        b.setId(1L);
        b.setTitle("T");

        when(svc.getAllBooks(PageRequest.of(0,10))).thenReturn(new PageImpl<>(List.of(b)));
        assertEquals(1, c.getAllBooks(PageRequest.of(0,10)).getTotalElements());

        when(svc.getBookById(1L)).thenReturn(b);
        assertEquals(b, c.getBookById(1L));

        BookCreateRequest req = new BookCreateRequest();
        when(svc.createBook(req)).thenReturn(b);
        assertEquals(b, c.createBook(req));

        when(svc.updateBook(1L, b)).thenReturn(b);
        assertEquals(b, c.updateBook(1L, b));

        // void delete
        c.deleteBook(1L);

        when(svc.searchBooks("x")).thenReturn(List.of(b));
        assertEquals(1, c.searchBooks("x").size());

        when(svc.findByApiId("aid")).thenReturn(Optional.of(b));
        assertTrue(c.getByApiId("aid").getStatusCode().is2xxSuccessful());

        OpenLibraryBookDto dto = new OpenLibraryBookDto();
        when(svc.importFromOpenLibrary(dto)).thenReturn(b);
        assertEquals(b, c.importFromOpenLibrary(dto));
    }
}
