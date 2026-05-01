package com.tfg.lunaris_backend.presentation.controller;

import com.tfg.lunaris_backend.domain.dto.SagaScrapedDto;
import com.tfg.lunaris_backend.domain.model.Saga;
import com.tfg.lunaris_backend.domain.service.SagaScrapingService;
import com.tfg.lunaris_backend.domain.service.SagaService;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test para {@link SagaController}.
 */
class SagaControllerTest {

    /**
     * Verifica que los métodos del controlador delegan correctamente en el servicio y que el scraping funciona.
     */
    @Test
    void delegatesAndScrape() {
        SagaService svc = mock(SagaService.class);
        SagaScrapingService ss = mock(SagaScrapingService.class);
        SagaController c = new SagaController();
        ReflectionTestUtils.setField(c, "sagaService", svc);
        ReflectionTestUtils.setField(c, "sagaScrapingService", ss);

        Saga s = new Saga();
        s.setId(5L);
        when(svc.getAllSagas()).thenReturn(List.of(s));
        assertEquals(1, c.getAllSagas().size());

        when(svc.getSagaById(5L)).thenReturn(s);
        assertEquals(s, c.getSagaById(5L));

        when(svc.createSaga(s)).thenReturn(s);
        assertEquals(s, c.createSaga(s));

        when(svc.updateSaga(5L, s)).thenReturn(s);
        assertEquals(s, c.updateSaga(5L, s));

        c.deleteSaga(5L);

        SagaScrapedDto dto = new SagaScrapedDto();
        when(ss.scrapeSaga("t", null)).thenReturn(dto);
        ResponseEntity<SagaScrapedDto> res = c.scrapeSaga("t", null);
        assertTrue(res.getStatusCode().is2xxSuccessful());
    }

    /**
     * Verifica que el scraping de una saga devuelve no content si el resultado es nulo.
     */
    @Test
    void scrapeSaga_nullResult_returnsNoContent() {
        SagaService svc = mock(SagaService.class);
        SagaScrapingService ss = mock(SagaScrapingService.class);
        SagaController c = new SagaController();
        ReflectionTestUtils.setField(c, "sagaService", svc);
        ReflectionTestUtils.setField(c, "sagaScrapingService", ss);

        when(ss.scrapeSaga("unknown", null)).thenReturn(null);
        var res = c.scrapeSaga("unknown", null);
        assertEquals(204, res.getStatusCode().value());
    }
}
