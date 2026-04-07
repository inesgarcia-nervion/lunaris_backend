package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.model.Review;
import com.tfg.lunaris_backend.domain.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReviewControllerTest {

    @Test
    void delegatesAndDelete() {
        ReviewService svc = mock(ReviewService.class);
        ReviewController c = new ReviewController();
        ReflectionTestUtils.setField(c, "reviewService", svc);

        Review r = new Review();
        r.setId(6L);
        r.setUsername("revuser");

        when(svc.getAllReviews()).thenReturn(List.of(r));
        assertEquals(1, c.getAllReviews().size());

        when(svc.getReviewById(6L)).thenReturn(r);
        assertEquals(r, c.getReviewById(6L));

        when(svc.getReviewsByBookApiId("api"))
                .thenReturn(List.of(r));
        assertEquals(1, c.getReviewsByBookApiId("api").size());

        when(svc.createReview(r)).thenReturn(r);
        assertEquals(r, c.createReview(r));

        when(svc.updateReview(6L, r)).thenReturn(r);
        assertEquals(r, c.updateReview(6L, r));

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("revuser");
        when(auth.getAuthorities()).thenReturn(java.util.Collections.emptyList());
        when(svc.getReviewById(6L)).thenReturn(r);

        c.deleteReview(6L, auth);
        verify(svc).deleteReview(6L);
    }

    @Test
    void deleteReview_asAdmin_succeeds() {
        ReviewService svc = mock(ReviewService.class);
        ReviewController c = new ReviewController();
        ReflectionTestUtils.setField(c, "reviewService", svc);

        Review r = new Review(); r.setId(7L); r.setUsername("someone");
        when(svc.getReviewById(7L)).thenReturn(r);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("admin");
        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(auth).getAuthorities();

        c.deleteReview(7L, auth);
        verify(svc).deleteReview(7L);
    }

    @Test
    void deleteReview_differentUser_throwsForbidden() {
        ReviewService svc = mock(ReviewService.class);
        ReviewController c = new ReviewController();
        ReflectionTestUtils.setField(c, "reviewService", svc);

        Review r = new Review(); r.setId(8L); r.setUsername("owner");
        when(svc.getReviewById(8L)).thenReturn(r);

        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("notowner");
        when(auth.getAuthorities()).thenReturn(java.util.Collections.emptyList());

        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> c.deleteReview(8L, auth));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void deleteReview_authNull_throwsForbidden() {
        ReviewService svc = mock(ReviewService.class);
        ReviewController c = new ReviewController();
        ReflectionTestUtils.setField(c, "reviewService", svc);

        Review r = new Review(); r.setId(9L); r.setUsername("owner");
        when(svc.getReviewById(9L)).thenReturn(r);

        var ex = assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> c.deleteReview(9L, null));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
