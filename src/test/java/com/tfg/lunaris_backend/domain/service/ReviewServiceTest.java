package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.ReviewRepository;
import com.tfg.lunaris_backend.domain.model.Review;
import com.tfg.lunaris_backend.presentation.exceptions.ReviewNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository repo;

    @Mock
    private org.springframework.web.client.RestTemplate restTemplate;

    @InjectMocks
    private ReviewService svc;

    @Test
    void getAllAndByBookId_withoutEnrichment() {
        Review r = new Review(); r.setBookApiId(null); r.setBookTitle("T"); r.setCoverUrl("C");
        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));
        assertFalse(svc.getAllReviews().isEmpty());

        when(repo.findByBookApiId("X")).thenReturn(List.of());
        assertTrue(svc.getReviewsByBookApiId("X").isEmpty());
    }

    @Test
    void getReviewByIdFoundAndNotFound() {
        Review r = new Review(); r.setComment("c");
        when(repo.findById(1L)).thenReturn(Optional.of(r));
        assertEquals("c", svc.getReviewById(1L).getComment());

        when(repo.findById(2L)).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class, () -> svc.getReviewById(2L));
    }

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

    @Test
    void enrichMissingBookData_callsOpenLibraryAndSaves() {
        Review r = new Review();
        r.setBookApiId("/works/OL123W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("title", "BookTitle");
        body.put("covers", java.util.List.of(111));

        org.springframework.http.ResponseEntity<java.util.Map> resp = org.springframework.http.ResponseEntity.ok(body);

        when(restTemplate.getForEntity(contains("OL123W"), eq(java.util.Map.class))).thenReturn(resp);

        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = svc.getAllReviews();
        assertFalse(res.isEmpty());
        assertEquals("BookTitle", res.get(0).getBookTitle());
        assertTrue(res.get(0).getCoverUrl().contains("covers.openlibrary.org"));
        verify(repo).save(any());
    }

    @Test
    void enrichMissingBookData_nonValidWorkKey_skipped() {
        // workKey that doesn't start with OL or doesn't end with W → skip
        Review r = new Review();
        r.setBookApiId("invalid-id");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));

        svc.getAllReviews();

        verify(restTemplate, never()).getForEntity(anyString(), any());
    }

    @Test
    void enrichMissingBookData_coversEmptyList_usesFallbackUrl() {
        Review r = new Review();
        r.setBookApiId("OL456W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("title", "TitleX");
        body.put("covers", java.util.List.of()); // empty covers

        org.springframework.http.ResponseEntity<java.util.Map> resp = org.springframework.http.ResponseEntity.ok(body);
        when(restTemplate.getForEntity(contains("OL456W"), eq(java.util.Map.class))).thenReturn(resp);
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var res = svc.getAllReviews();
        assertFalse(res.isEmpty());
        assertTrue(res.get(0).getCoverUrl().contains("OL456W"));
    }

    @Test
    void enrichMissingBookData_nonOkResponse_skipped() {
        Review r = new Review();
        r.setBookApiId("OL789W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));

        org.springframework.http.ResponseEntity<java.util.Map> resp =
                org.springframework.http.ResponseEntity.status(org.springframework.http.HttpStatus.NOT_FOUND)
                        .build();
        when(restTemplate.getForEntity(contains("OL789W"), eq(java.util.Map.class))).thenReturn(resp);

        svc.getAllReviews();

        verify(repo, never()).save(any());
    }

    @Test
    void enrichMissingBookData_nullReviewsList_returnsEarly() {
        // passing null/empty list should return early without error
        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of());
        assertTrue(svc.getAllReviews().isEmpty());
    }

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
        details.setBookTitle(null); // should NOT overwrite
        details.setCoverUrl(null);  // should NOT overwrite

        Review updated = svc.updateReview(10L, details);
        assertEquals("original-title", updated.getBookTitle());
        assertEquals("original-cover", updated.getCoverUrl());
        assertEquals("new-comment", updated.getComment());
    }

    @Test
    void updateReviewNotFoundThrows() {
        when(repo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class, () -> svc.updateReview(99L, new Review()));
    }

    @Test
    void enrichMissingBookData_restThrowsException_isIgnored() {
        Review r = new Review();
        r.setBookApiId("OL999W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));
        when(restTemplate.getForEntity(anyString(), eq(java.util.Map.class)))
                .thenThrow(new RuntimeException("connection refused"));

        // Should not throw - exception is swallowed
        var result = svc.getAllReviews();
        assertFalse(result.isEmpty());
        verify(repo, never()).save(any());
    }

    @Test
    void enrichMissingBookData_nullBody_skipped() {
        Review r = new Review();
        r.setBookApiId("OL888W");
        r.setBookTitle(null);
        r.setCoverUrl(null);

        when(repo.findAllByOrderByIdDesc()).thenReturn(List.of(r));
        org.springframework.http.ResponseEntity<java.util.Map> resp =
                org.springframework.http.ResponseEntity.ok(null); // null body
        when(restTemplate.getForEntity(anyString(), eq(java.util.Map.class))).thenReturn(resp);

        svc.getAllReviews();
        verify(repo, never()).save(any());
    }

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
        details.setBookTitle("new-title");   // not null → covers line 132
        details.setCoverUrl("new-cover");    // not null → covers line 134

        Review updated = svc.updateReview(20L, details);
        assertEquals("new-title", updated.getBookTitle());
        assertEquals("new-cover", updated.getCoverUrl());
    }
}
