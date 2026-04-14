package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.ReviewRepository;
import com.tfg.lunaris_backend.domain.model.Review;
import com.tfg.lunaris_backend.presentation.exceptions.ReviewNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test para la clase ReviewService.
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repo;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReviewService svc;

    /**
     * Verifica que se retornan todas las reseñas y las reseñas por ID de libro sin enriquecimiento.
     */
    @Test
    void getAllAndByBookId_withoutEnrichment() {
        Review r = new Review(); r.setBookApiId(null); r.setBookTitle("T"); r.setCoverUrl("C");
        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));
        assertFalse(svc.getAllReviews().isEmpty());

        when(repo.findByBookApiId("X")).thenReturn(List.of());
        assertTrue(svc.getReviewsByBookApiId("X").isEmpty());
    }

    /**
     * Verifica que se retorna la reseña por ID cuando existe y lanza una excepción cuando no existe.
     */
    @Test
    void getReviewByIdFoundAndNotFound() {
        Review r = new Review(); r.setComment("c");
        when(repo.findById(1L)).thenReturn(Optional.of(r));
        assertEquals("c", svc.getReviewById(1L).getComment());

        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class, () -> svc.getReviewById(2L));
    }

    /**
     * Verifica la creación, actualización y eliminación de reseñas.
     */
    @Test
    void createUpdateDelete() {
        Review r = new Review(); r.setComment("x");
        when(repo.save(r)).thenReturn(r);
        assertSame(r, svc.createReview(r));

        when(repo.findById(3L)).thenReturn(Optional.of(r));
        Review details = new Review(); details.setComment("y"); details.setRating(5.0);
        when(repo.save(r)).thenReturn(r);
        Review up = svc.updateReview(3L, details);
        assertEquals("y", up.getComment());

        svc.deleteReview(4L);
        verify(repo).deleteById(4L);
    }

    /**
     * Verifica que se llama a OpenLibrary para enriquecer los datos del libro y se guarda la reseña actualizada.
     */
    @Test
    void enrichMissingBookData_callsOpenLibraryAndSaves() {
        Review r = new Review();
        r.setBookApiId("/works/OL123W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));

        Map<String, Object> body = new HashMap<>();
        body.put("title", "BookTitle");
        body.put("covers", List.of(111));

        ResponseEntity<Map> resp = ResponseEntity.ok(body);

        when(restTemplate.getForEntity(contains("OL123W"), eq(Map.class))).thenReturn(resp);

        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = svc.getAllReviews();
        assertFalse(res.isEmpty());
        assertEquals("BookTitle", res.get(0).getBookTitle());
        assertTrue(res.get(0).getCoverUrl().contains("covers.openlibrary.org"));
        verify(repo).save(any());
    }

    /**
     * Verifica que se omite el enriquecimiento de datos cuando el workKey no es válido.
     */
    @Test
    void enrichMissingBookData_nonValidWorkKey_skipped() {
        Review r = new Review();
        r.setBookApiId("invalid-id");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));

        svc.getAllReviews();

        verify(restTemplate, never()).getForEntity(anyString(), any());
    }

    /**
     * Verifica que se utiliza una URL de respaldo cuando la lista de portadas está vacía.
     */
    @Test
    void enrichMissingBookData_coversEmptyList_usesFallbackUrl() {
        Review r = new Review();
        r.setBookApiId("OL456W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));

        Map<String, Object> body = new HashMap<>();
        body.put("title", "TitleX");
        body.put("covers", List.of()); 

        ResponseEntity<Map> resp = ResponseEntity.ok(body);
        when(restTemplate.getForEntity(contains("OL456W"), eq(Map.class))).thenReturn(resp);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = svc.getAllReviews();
        assertFalse(res.isEmpty());
        assertTrue(res.get(0).getCoverUrl().contains("OL456W"));
    }

    /**
     * Verifica que se omite el enriquecimiento de datos cuando la respuesta de OpenLibrary no es OK.
     */
    @Test
    void enrichMissingBookData_nonOkResponse_skipped() {
        Review r = new Review();
        r.setBookApiId("OL789W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));

        ResponseEntity<Map> resp =
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .build();
        when(restTemplate.getForEntity(contains("OL789W"), eq(Map.class))).thenReturn(resp);

        svc.getAllReviews();

        verify(repo, never()).save(any());
    }

    /**
     * Verifica que se omite el enriquecimiento de datos cuando la lista de reseñas es nula.
     */
    @Test
    void enrichMissingBookData_nullReviewsList_returnsEarly() {
        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of());
        assertTrue(svc.getAllReviews().isEmpty());
    }

    /**
     * Verifica que se omite la actualización del título y la portada cuando son nulos.
     */
    @Test
    void updateReview_nullBookTitleAndCoverUrl_notUpdated() {
        Review r = new Review();
        r.setBookTitle("original-title");
        r.setCoverUrl("original-cover");
        r.setComment("c"); r.setRating(4.0);

        when(repo.findById(10L)).thenReturn(Optional.of(r));
        when(repo.save(r)).thenReturn(r);

        Review details = new Review();
        details.setComment("new-comment");
        details.setRating(3.0);
        details.setBookTitle(null); 
        details.setCoverUrl(null); 

        Review updated = svc.updateReview(10L, details);
        assertEquals("original-title", updated.getBookTitle());
        assertEquals("original-cover", updated.getCoverUrl());
        assertEquals("new-comment", updated.getComment());
    }

    /**
     * Verifica que se lanza una excepción cuando la reseña no se encuentra.
     */
    @Test
    void updateReviewNotFoundThrows() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class, () -> svc.updateReview(99L, new Review()));
    }

    /**
     * Verifica que se omite el enriquecimiento de datos cuando ocurre una excepción en la llamada REST.
     */
    @Test
    void enrichMissingBookData_restThrowsException_isIgnored() {
        Review r = new Review();
        r.setBookApiId("OL999W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));
        when(restTemplate.getForEntity(anyString(), eq(Map.class)))
                .thenThrow(new RuntimeException("connection refused"));

        var result = svc.getAllReviews();
        assertFalse(result.isEmpty());
        verify(repo, never()).save(any());
    }

    /**
     * Verifica que se omite el enriquecimiento de datos cuando el cuerpo de la respuesta es nulo.
     */
    @Test
    void enrichMissingBookData_nullBody_skipped() {
        Review r = new Review();
        r.setBookApiId("OL888W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));
        ResponseEntity<Map> resp =
                ResponseEntity.ok(null); 
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(resp);

        svc.getAllReviews();
        verify(repo, never()).save(any());
    }

    /**
     * Verifica que se actualiza el título y la portada cuando no son nulos.
     */
    @Test
    void updateReview_withNonNullBookTitleAndCoverUrl_overwritesExisting() {
        Review r = new Review();
        r.setBookTitle("old-title");
        r.setCoverUrl("old-cover");
        r.setComment("c"); r.setRating(4.0);

        when(repo.findById(20L)).thenReturn(Optional.of(r));
        when(repo.save(r)).thenReturn(r);

        Review details = new Review();
        details.setComment("new");
        details.setRating(5.0);
        details.setBookTitle("new-title");  
        details.setCoverUrl("new-cover");  

        Review updated = svc.updateReview(20L, details);
        assertEquals("new-title", updated.getBookTitle());
        assertEquals("new-cover", updated.getCoverUrl());
    }
}
