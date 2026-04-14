package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.BookRepository;
import com.tfg.lunaris_backend.data.repository.GenreRepository;
import com.tfg.lunaris_backend.domain.dto.BookCreateRequest;
import com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto;
import com.tfg.lunaris_backend.domain.model.Book;
import com.tfg.lunaris_backend.domain.model.Genre;
import com.tfg.lunaris_backend.presentation.exceptions.BookNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para la clase BookService.
 */
@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepo;

    @Mock
    private GenreRepository genreRepo;

    @InjectMocks
    private BookService svc;

    /**
     * Verifica los flujos básicos y la búsqueda por ID de API.
     */
    @Test
    void basicFlowsAndFindByApiId() {
        Book b = new Book(); b.setTitle("T"); b.setApiId("A1");
        when(bookRepo.findAll()).thenReturn(List.of(b));
        assertEquals(1, svc.getAllBooks().size());

        when(bookRepo.findAll(Pageable.unpaged())).thenReturn(new PageImpl<>(List.of(b)));
        assertEquals(1, svc.getAllBooks(Pageable.unpaged()).getContent().size());

        when(bookRepo.findById(1L)).thenReturn(Optional.of(b));
        assertSame(b, svc.getBookById(1L));

        when(bookRepo.findByApiId("A1")).thenReturn(Optional.of(b));
        assertTrue(svc.findByApiId("A1").isPresent());
    }

    /**
     * Verifica que se lanza una excepción cuando se intenta obtener un libro por un ID que no existe.
     */
    @Test
    void getBookByIdNotFoundThrows() {
        when(bookRepo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> svc.getBookById(2L));
    }

    /**
     * Verifica que se pueden crear libros con géneros y importar desde OpenLibrary correctamente.
     */
    @Test
    void createBookWithGenresAndImport() {
        BookCreateRequest req = new BookCreateRequest();
        req.setTitle("New"); req.setAuthor("Auth");
        req.setGenreIds(List.of(5L));

        Genre g = new Genre(); g.setName("G");
        when(genreRepo.findById(5L)).thenReturn(Optional.of(g));
        when(bookRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Book created = svc.createBook(req);
        assertEquals("New", created.getTitle());

        OpenLibraryBookDto dto = new OpenLibraryBookDto();
        dto.setKey("K1"); dto.setTitle("T1"); dto.setAuthorNames(List.of("A1"));
        Book existing = new Book(); existing.setApiId("K1");
        when(bookRepo.findAll()).thenReturn(List.of(existing));
        Book imp = svc.importFromOpenLibrary(dto);
        assertEquals(existing, imp);
    }

    /**
     * Verifica que se pueden actualizar y eliminar libros correctamente.
     */
    @Test
    void updateAndDelete() {
        Book b = new Book(); b.setTitle("Old");
        when(bookRepo.findById(3L)).thenReturn(Optional.of(b));
        when(bookRepo.save(b)).thenReturn(b);
        Book details = new Book(); details.setTitle("New");
        Book up = svc.updateBook(3L, details);
        assertEquals("New", up.getTitle());

        svc.deleteBook(4L);
        verify(bookRepo).deleteById(4L);
    }

    /**
     * Verifica que la búsqueda de libros se delega correctamente al repositorio.
     */
    @Test
    void searchBooksDelegates() {
        when(bookRepo.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase("q","q"))
                .thenReturn(List.of(new Book()));
        assertFalse(svc.searchBooks("q").isEmpty());
    }

    /**
     * Verifica que la búsqueda de libros con paginación se delega correctamente al repositorio.
     */
    @Test
    void searchBooksWithPageable() {
        when(bookRepo.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCase(eq("q"), eq("q"),
                any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new Book())));
        assertFalse(svc.searchBooks("q", Pageable.unpaged()).isEmpty());
    }

    /**
     * Verifica que se pueden importar libros desde OpenLibrary correctamente.
     */
    @Test
    void importFromOpenLibrary_newBook_creates() {
        OpenLibraryBookDto dto = new OpenLibraryBookDto();
        dto.setKey("K2"); dto.setTitle("NewTitle"); dto.setAuthorNames(List.of("Auth2"));
        dto.setDescription("desc");
        dto.setRatingsAverage(4.5);
        when(bookRepo.findAll()).thenReturn(List.of()); // no existing
        when(bookRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Book created = svc.importFromOpenLibrary(dto);
        assertEquals("NewTitle", created.getTitle());
        assertEquals("desc", created.getDescription());
        assertEquals(4.5, created.getScore());
    }

    /**
     * Verifica que se pueden importar libros desde OpenLibrary correctamente cuando la descripción y la puntuación son nulas.
     */
    @Test
    void importFromOpenLibrary_newBook_nullDescriptionAndScore() {
        OpenLibraryBookDto dto = new OpenLibraryBookDto();
        dto.setKey("K3"); dto.setTitle("T");
        dto.setDescription(null);
        dto.setRatingsAverage(null);
        when(bookRepo.findAll()).thenReturn(List.of());
        when(bookRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Book b = svc.importFromOpenLibrary(dto);
        assertEquals("", b.getDescription());
        assertEquals(0.0, b.getScore());
    }

    /**
     * Verifica que se pueden crear libros con un ID de API explícito.
     */
    @Test
    void createBook_withExplicitApiId() {
        BookCreateRequest req = new BookCreateRequest();
        req.setTitle("T"); req.setApiId("myApiId");
        when(bookRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Book created = svc.createBook(req);
        assertEquals("myApiId", created.getApiId());
    }

    /**
     * Verifica que se generan IDs de API personalizados cuando el ID proporcionado está en blanco.
     */
    @Test
    void createBook_withBlankApiId_generatesCustomApiId() {
        BookCreateRequest req = new BookCreateRequest();
        req.setTitle("T"); req.setApiId("   ");
        when(bookRepo.save(any())).thenAnswer(i -> i.getArgument(0));

        Book created = svc.createBook(req);
        assertTrue(created.getApiId().startsWith("custom-"));
    }

    /**
     * Verifica que se lanza una excepción cuando se intenta actualizar un libro que no existe.
     */
    @Test
    void updateBookNotFoundThrows() {
        when(bookRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> svc.updateBook(99L, new Book()));
    }
}
