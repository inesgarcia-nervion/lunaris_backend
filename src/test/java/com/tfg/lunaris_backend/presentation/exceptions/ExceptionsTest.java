package com.tfg.lunaris_backend.presentation.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test para {@link GlobalExceptionHandler}.
 */
class ExceptionsTest {

    /**
     * Verifica que los mensajes de las excepciones y el manejador global funcionan correctamente.
     */
    @Test
    void exceptionMessagesAndHandler() {
        BookNotFoundException be = new BookNotFoundException("b not found");
        AuthorNotFoundException ae = new AuthorNotFoundException("a not found");
        GenreNotFoundException ge = new GenreNotFoundException("g not found");
        ReviewNotFoundException re = new ReviewNotFoundException("r not found");
        UserNotFoundException ue = new UserNotFoundException("u not found");
        UserListNotFoundException ule = new UserListNotFoundException("ul not found");
        SagaNotFoundException se = new SagaNotFoundException("s not found");

        assertEquals("b not found", be.getMessage());
        assertEquals("a not found", ae.getMessage());
        assertEquals("g not found", ge.getMessage());
        assertEquals("r not found", re.getMessage());
        assertEquals("u not found", ue.getMessage());
        assertEquals("ul not found", ule.getMessage());
        assertEquals("s not found", se.getMessage());

        GlobalExceptionHandler geh = new GlobalExceptionHandler();
        ResponseEntity<String> resp = geh.handleBookNotFoundException(be);
        assertEquals(404, resp.getStatusCode().value());
        assertEquals("b not found", resp.getBody());

        resp = geh.handleAuthorNotFoundException(ae);
        assertEquals(404, resp.getStatusCode().value());

        resp = geh.handleGenreNotFoundException(ge);
        assertEquals(404, resp.getStatusCode().value());

        resp = geh.handleReviewNotFoundException(re);
        assertEquals(404, resp.getStatusCode().value());

        resp = geh.handleUserNotFoundException(ue);
        assertEquals(404, resp.getStatusCode().value());

        resp = geh.handleUserListNotFoundException(ule);
        assertEquals(404, resp.getStatusCode().value());

        resp = geh.handleSagaNotFoundException(se);
        assertEquals(404, resp.getStatusCode().value());
    }
}
