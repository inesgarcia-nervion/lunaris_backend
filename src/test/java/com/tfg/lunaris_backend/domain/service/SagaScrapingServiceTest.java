package com.tfg.lunaris_backend.domain.service;

import com.tfg.lunaris_backend.data.repository.SagaRepository;
import com.tfg.lunaris_backend.domain.dto.SagaScrapedDto;
import com.tfg.lunaris_backend.domain.model.Saga;
import com.tfg.lunaris_backend.domain.model.SagaBook;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SagaScrapingServiceTest {

    @Mock
    private SagaRepository repo;

    @InjectMocks
    private SagaScrapingService svc;

    // ── Cache: valid ────────────────────────────────────────────────────────────

    @Test
    void returnsCachedSagaWhenValid() {
        Saga saga = new Saga();
        saga.setName("SagaX");
        SagaBook b1 = new SagaBook(); b1.setOrderNumber("1"); b1.setTitle("T1"); b1.setSaga(saga);
        SagaBook b2 = new SagaBook(); b2.setOrderNumber("2"); b2.setTitle("T2"); b2.setSaga(saga);
        SagaBook b3 = new SagaBook(); b3.setOrderNumber("3"); b3.setTitle("T3"); b3.setSaga(saga);
        saga.getBooks().add(b1); saga.getBooks().add(b2); saga.getBooks().add(b3);

        when(repo.findByBookTitleIgnoreCase("T1")).thenReturn(Optional.of(saga));

        SagaScrapedDto dto = svc.scrapeSaga("T1", null);
        assertNotNull(dto);
        assertEquals("SagaX", dto.getSagaName());
        assertEquals(3, dto.getBooks().size());
    }

    // ── Cache: stale (unfiltered orderNumber → null) – fallback gets IOException ─

    @Test
    void scrapeSaga_staleCacheUnfiltered_ioException_returnsNull() throws Exception {
        Saga saga = new Saga();
        saga.setName("SagaY");
        SagaBook b = new SagaBook();
        b.setOrderNumber(null);  // hasUnfiltered = true
        b.setSaga(saga);
        saga.getBooks().add(b);
        when(repo.findByBookTitleIgnoreCase("T2")).thenReturn(Optional.of(saga));

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenThrow(new IOException("network error"));

            SagaScrapedDto result = svc.scrapeSaga("T2", null);
            assertNull(result);
            verify(repo).delete(saga);
        }
    }

    // ── Cache: stale (size ≤ 2, no unfiltered) – fallback gets IOException ────

    @Test
    void scrapeSaga_staleCache_sizeTwo_ioException_returnsNull() throws Exception {
        Saga saga = new Saga();
        saga.setName("SagaZ");
        SagaBook b1 = new SagaBook(); b1.setOrderNumber("1"); b1.setSaga(saga);
        SagaBook b2 = new SagaBook(); b2.setOrderNumber("2"); b2.setSaga(saga);
        saga.getBooks().add(b1); saga.getBooks().add(b2);
        when(repo.findByBookTitleIgnoreCase("T3")).thenReturn(Optional.of(saga));

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenThrow(new IOException("network error"));

            SagaScrapedDto result = svc.scrapeSaga("T3", null);
            assertNull(result);
            verify(repo).delete(saga);
        }
    }

    // ── No cache: IOException during search ─────────────────────────────────────

    @Test
    void scrapeSaga_noCache_ioException_returnsNull() throws Exception {
        when(repo.findByBookTitleIgnoreCase("T4")).thenReturn(Optional.empty());

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenThrow(new IOException("network error"));

            SagaScrapedDto result = svc.scrapeSaga("T4", null);
            assertNull(result);
        }
    }

    // ── No cache: empty search results (with author) ─────────────────────────────

    @Test
    void scrapeSaga_noCache_emptyBookUrls_withAuthor_returnsNull() throws Exception {
        when(repo.findByBookTitleIgnoreCase("T5")).thenReturn(Optional.empty());

        Document emptyDoc = Jsoup.parse("<html><body></body></html>");

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenReturn(emptyDoc);

            SagaScrapedDto result = svc.scrapeSaga("T5", "AuthorName");
            assertNull(result);
        }
    }

    // ── No cache: book URL found but no series link ──────────────────────────────

    @Test
    void scrapeSaga_noCache_bookUrlFound_noSeriesLink_returnsNull() throws Exception {
        when(repo.findByBookTitleIgnoreCase("T6")).thenReturn(Optional.empty());

        String searchHtml = "<html><body><table class='tableList'>"
                + "<tr itemscope><td><a class='bookTitle' href='/book/show/123'>TestBook</a></td></tr>"
                + "</table></body></html>";
        String bookHtml = "<html><body><p>Book page without series link</p></body></html>";

        Document searchDoc = Jsoup.parse(searchHtml);
        Document bookDoc = Jsoup.parse(bookHtml);

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenReturn(searchDoc).thenReturn(bookDoc);

            SagaScrapedDto result = svc.scrapeSaga("T6", null);
            assertNull(result);
        }
    }

    // ── Full happy path: book → series link → series page → save → return dto ──

    @Test
    void scrapeSaga_fullPath_createsAndReturnsSaga() throws Exception {
        when(repo.findByBookTitleIgnoreCase("Book1")).thenReturn(Optional.empty());
        when(repo.findByName("SagaName")).thenReturn(Optional.empty());
        when(repo.save(any(Saga.class))).thenAnswer(i -> i.getArgument(0));

        String searchHtml = "<html><body><table class='tableList'>"
                + "<tr itemscope><td><a class='bookTitle' href='/book/show/123'>Book1</a></td></tr>"
                + "</table></body></html>";
        String bookHtml = "<html><body><a href='/series/456-saganame'>SagaName Series</a></body></html>";
        String seriesHtml = "<html><head><title>SagaName Series | Goodreads</title></head><body>"
                + "<div class='responsiveSeriesHeader__title'><h1>SagaName Series</h1></div>"
                + "<div class='listWithDividers__item'><h3>Book 1</h3>"
                + "<span itemprop='name'>Title One</span></div>"
                + "<div class='listWithDividers__item'><h3>Book 2</h3>"
                + "<span itemprop='name'>Title Two</span></div>"
                + "<div class='listWithDividers__item'><h3>Book 3</h3>"
                + "<span itemprop='name'>Title Three</span></div>"
                + "</body></html>";

        Document searchDoc = Jsoup.parse(searchHtml);
        Document bookDoc = Jsoup.parse(bookHtml);
        Document seriesDoc = Jsoup.parse(seriesHtml);

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenReturn(searchDoc).thenReturn(bookDoc).thenReturn(seriesDoc);

            SagaScrapedDto result = svc.scrapeSaga("Book1", null);
            assertNotNull(result);
            assertEquals("SagaName", result.getSagaName());
            verify(repo).save(any(Saga.class));
        }
    }

    // ── Full path: series page → scrapeSeriesPage returns null (no saga name) ──

    @Test
    void scrapeSaga_scrapeSeriesPageReturnsNull_returnsNull() throws Exception {
        when(repo.findByBookTitleIgnoreCase("T7")).thenReturn(Optional.empty());

        String searchHtml = "<html><body><table class='tableList'>"
                + "<tr itemscope><td><a class='bookTitle' href='/book/show/999'>T7</a></td></tr>"
                + "</table></body></html>";
        String bookHtml = "<html><body><a href='/series/999-unknown'>Link</a></body></html>";
        // series page with no recognisable series name
        String seriesHtml = "<html><head><title>Some Page</title></head><body></body></html>";

        Document searchDoc = Jsoup.parse(searchHtml);
        Document bookDoc = Jsoup.parse(bookHtml);
        Document seriesDoc = Jsoup.parse(seriesHtml);

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenReturn(searchDoc).thenReturn(bookDoc).thenReturn(seriesDoc);

            SagaScrapedDto result = svc.scrapeSaga("T7", null);
            assertNull(result);
        }
    }

    // ── saveSagaToDb: existing saga has ≥ new books → skip ────────────────────

    @Test
    void saveSagaToDb_existingWithMoreOrEqualBooks_skip() throws Exception {
        SagaScrapedDto dto = new SagaScrapedDto("S1", List.of());
        Saga existing = new Saga();
        existing.setName("S1");
        SagaBook b = new SagaBook(); b.setSaga(existing);
        existing.getBooks().add(b);
        when(repo.findByName("S1")).thenReturn(Optional.of(existing));

        Method m = SagaScrapingService.class.getDeclaredMethod("saveSagaToDb", SagaScrapedDto.class);
        m.setAccessible(true);
        m.invoke(svc, dto);

        verify(repo, never()).save(any());
    }

    // ── saveSagaToDb: existing saga has fewer books → update ─────────────────

    @Test
    void saveSagaToDb_existingWithFewerBooks_update() throws Exception {
        SagaScrapedDto.SagaBookEntry entry = new SagaScrapedDto.SagaBookEntry("T", "A", "1", 100, 2020, "url");
        SagaScrapedDto dto = new SagaScrapedDto("S2", List.of(entry, entry));
        Saga existing = new Saga();
        existing.setName("S2");
        SagaBook b = new SagaBook(); b.setSaga(existing);
        existing.getBooks().add(b); // 1 book < 2 in dto → update
        when(repo.findByName("S2")).thenReturn(Optional.of(existing));
        when(repo.save(existing)).thenReturn(existing);

        Method m = SagaScrapingService.class.getDeclaredMethod("saveSagaToDb", SagaScrapedDto.class);
        m.setAccessible(true);
        m.invoke(svc, dto);

        verify(repo).save(existing);
    }

    // ── saveSagaToDb: no existing → create new ───────────────────────────────

    @Test
    void saveSagaToDb_noExisting_creates() throws Exception {
        SagaScrapedDto.SagaBookEntry entry = new SagaScrapedDto.SagaBookEntry("T", "A", "1", 100, 2020, "url");
        SagaScrapedDto dto = new SagaScrapedDto("S3", List.of(entry));
        when(repo.findByName("S3")).thenReturn(Optional.empty());
        when(repo.save(any(Saga.class))).thenAnswer(i -> i.getArgument(0));

        Method m = SagaScrapingService.class.getDeclaredMethod("saveSagaToDb", SagaScrapedDto.class);
        m.setAccessible(true);
        m.invoke(svc, dto);

        verify(repo).save(any(Saga.class));
    }

    // ── filterSagaBooks (private) ────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void filterSagaBooks_filtersInvalidAndDuplicates() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("filterSagaBooks", List.class);
        m.setAccessible(true);

        List<SagaScrapedDto.SagaBookEntry> books = new ArrayList<>();

        SagaScrapedDto.SagaBookEntry nullOrder  = new SagaScrapedDto.SagaBookEntry(); nullOrder.setOrderNumber(null);
        SagaScrapedDto.SagaBookEntry blankOrder = new SagaScrapedDto.SagaBookEntry(); blankOrder.setOrderNumber("  ");
        SagaScrapedDto.SagaBookEntry rangeOrder = new SagaScrapedDto.SagaBookEntry(); rangeOrder.setOrderNumber("1-2");
        SagaScrapedDto.SagaBookEntry valid1     = new SagaScrapedDto.SagaBookEntry(); valid1.setOrderNumber("1");
        SagaScrapedDto.SagaBookEntry dup1       = new SagaScrapedDto.SagaBookEntry(); dup1.setOrderNumber("1");
        SagaScrapedDto.SagaBookEntry valid2     = new SagaScrapedDto.SagaBookEntry(); valid2.setOrderNumber("2");

        books.add(nullOrder); books.add(blankOrder); books.add(rangeOrder);
        books.add(valid1); books.add(dup1); books.add(valid2);

        List<SagaScrapedDto.SagaBookEntry> filtered =
                (List<SagaScrapedDto.SagaBookEntry>) m.invoke(svc, books);

        assertEquals(2, filtered.size());
    }

    // ── extractSeriesName (private) ──────────────────────────────────────────

    @Test
    void extractSeriesName_withHeaderWithoutSuffix() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractSeriesName",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        // Header ends with " Series" → suffix stripped
        Document doc1 = Jsoup.parse(
                "<div class='responsiveSeriesHeader__title'><h1>Magic Series</h1></div>");
        assertEquals("Magic", m.invoke(svc, doc1));

        // Header does NOT end with " Series" → kept as-is
        Document doc2 = Jsoup.parse(
                "<div class='responsiveSeriesHeader__title'><h1>Trilogy</h1></div>");
        assertEquals("Trilogy", m.invoke(svc, doc2));
    }

    @Test
    void extractSeriesName_fromTitle_andNull() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractSeriesName",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        // No header → falls back to page title that contains "Series"
        Document docTitle = Jsoup.parse(
                "<html><head><title>Magic Series by Author | Goodreads</title></head></html>");
        String result = (String) m.invoke(svc, docTitle);
        assertNotNull(result);

        // No header, page title has no "Series" → null
        Document docNoMatch = Jsoup.parse("<html><head><title>Some Page</title></head></html>");
        assertNull(m.invoke(svc, docNoMatch));
    }

    // ── extractOrderNumber (private) ─────────────────────────────────────────

    @Test
    void extractOrderNumber_withAndWithoutPattern() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractOrderNumber", String.class);
        m.setAccessible(true);

        assertNull(m.invoke(svc, (Object) null));
        assertEquals("1", m.invoke(svc, "Book Title (#1)"));
        assertEquals("2.5", m.invoke(svc, "Another Book (#2.5)"));
        assertNull(m.invoke(svc, "No pattern here"));
    }

    // ── cleanHtmlEntities (private) ──────────────────────────────────────────

    @Test
    void cleanHtmlEntities_replacesEntities() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("cleanHtmlEntities", String.class);
        m.setAccessible(true);

        assertNull(m.invoke(svc, (Object) null));
        String cleaned = (String) m.invoke(svc, "it&#39;s &amp; &quot;quoted&quot; &lt;tag&gt;");
        assertEquals("it's & \"quoted\" <tag>", cleaned);
    }

    // ── getTextNode (private) ────────────────────────────────────────────────

    @Test
    void getTextNode_nullAndPresent() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("getTextNode",
                tools.jackson.databind.JsonNode.class, String.class);
        m.setAccessible(true);

        tools.jackson.databind.ObjectMapper mapper = new tools.jackson.databind.ObjectMapper();
        tools.jackson.databind.JsonNode node = mapper.readTree("{\"key\":\"value\",\"nullKey\":null}");

        assertEquals("value", m.invoke(svc, node, "key"));
        assertNull(m.invoke(svc, node, "nullKey"));
        assertNull(m.invoke(svc, node, "missing"));
    }

    // ── extractBooksFromJson (private): no react elements → empty list ────────

    @Test
    @SuppressWarnings("unchecked")
    void extractBooksFromJson_noReactElements_returnsEmpty() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractBooksFromJson",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        Document doc = Jsoup.parse("<html><body></body></html>");
        List<?> result = (List<?>) m.invoke(svc, doc);
        assertTrue(result.isEmpty());
    }

    // ── extractBooksFromJson (private): react element with valid JSON ─────────

    @Test
    @SuppressWarnings("unchecked")
    void extractBooksFromJson_withValidJson_returnsBooks() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractBooksFromJson",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        String jsonProps = "{\"series\":[{\"book\":{"
                + "\"bookTitleBare\":\"My Title\","
                + "\"title\":\"My Title (Series #1)\","
                + "\"author\":{\"name\":\"The Author\"},"
                + "\"numPages\":300,"
                + "\"publicationDate\":\"2021\","
                + "\"bookUrl\":\"/book/123\"}}]}";
        String html = "<html><body>"
                + "<div data-react-class=\"ReactComponents.SeriesList\" data-react-props='"
                + jsonProps + "'></div></body></html>";

        Document doc = Jsoup.parse(html);
        List<SagaScrapedDto.SagaBookEntry> books =
                (List<SagaScrapedDto.SagaBookEntry>) m.invoke(svc, doc);

        assertFalse(books.isEmpty());
        assertEquals("My Title", books.get(0).getTitle());
        assertEquals("The Author", books.get(0).getAuthor());
        assertEquals("1", books.get(0).getOrderNumber());
        assertEquals(300, books.get(0).getPages());
        assertEquals(2021, books.get(0).getYear());
    }

    // ── extractBooksFromJson (private): react element with empty JSON props ──

    @Test
    @SuppressWarnings("unchecked")
    void extractBooksFromJson_emptyDataReactProps_returnsEmpty() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractBooksFromJson",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        // data-react-props is empty string → jsonStr.isEmpty() → continue
        String html = "<html><body>"
                + "<div data-react-class=\"ReactComponents.SeriesList\" data-react-props=''>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        List<?> result = (List<?>) m.invoke(svc, doc);
        assertTrue(result.isEmpty());
    }

    // ── extractBooksFromJson (private): valid JSON but no "series" array ──────

    @Test
    @SuppressWarnings("unchecked")
    void extractBooksFromJson_noSeriesArray_returnsEmpty() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractBooksFromJson",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        String html = "<html><body>"
                + "<div data-react-class=\"ReactComponents.SeriesList\" data-react-props='{\"other\":[]}'>"
                + "</div></body></html>";
        Document doc = Jsoup.parse(html);
        List<?> result = (List<?>) m.invoke(svc, doc);
        assertTrue(result.isEmpty());
    }

    // ── extractBooksFromJson: entry with no "book" node ──────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void extractBooksFromJson_entryWithNoBookNode_skipped() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractBooksFromJson",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        String jsonProps = "{\"series\":[{}]}"; // entry has no "book" key
        String html = "<html><body>"
                + "<div data-react-class=\"ReactComponents.SeriesList\" data-react-props='"
                + jsonProps + "'></div></body></html>";
        Document doc = Jsoup.parse(html);
        List<?> result = (List<?>) m.invoke(svc, doc);
        assertTrue(result.isEmpty());
    }

    // ── extractBooksFromHtml (private) ───────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void extractBooksFromHtml_parsesItems() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractBooksFromHtml",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        String html = "<html><body>"
                + "<div class='listWithDividers__item'><h3>Book 1</h3>"
                + "<span itemprop='name'>First Book</span>"
                + "<span itemprop='author'><span itemprop='name'>An Author</span></span>"
                + "<a itemprop='url' href='/book/1'></a></div>"
                + "<div class='listWithDividers__item'><h3>No number here !</h3>"
                + "</div>" // title is null → not added
                + "</body></html>";
        Document doc = Jsoup.parse(html);
        List<SagaScrapedDto.SagaBookEntry> books =
                (List<SagaScrapedDto.SagaBookEntry>) m.invoke(svc, doc);

        assertEquals(1, books.size());
        assertEquals("First Book", books.get(0).getTitle());
        assertEquals("An Author", books.get(0).getAuthor());
        assertEquals("1", books.get(0).getOrderNumber());
    }

    // ── findBookUrls: URL with filter keyword → continue (L191) + fallback (L201) ──

    @Test
    void findBookUrls_filteredUrl_triggersLine191AndFallbackLine201() throws Exception {
        when(repo.findByBookTitleIgnoreCase("TF")).thenReturn(Optional.empty());

        // All results have "summary" in href → all filtered → fallback (L201) used
        String searchHtml = "<html><body><table class='tableList'>"
                + "<tr itemscope><td><a class='bookTitle' href='/book/show/99-summary-guide'>TF</a></td></tr>"
                + "</table></body></html>";
        // Book page: no series link → findSeriesUrl returns null
        String bookHtml = "<html><body><p>No series here</p></body></html>";

        Document searchDoc = Jsoup.parse(searchHtml);
        Document bookDoc = Jsoup.parse(bookHtml);

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenReturn(searchDoc).thenReturn(bookDoc);

            SagaScrapedDto result = svc.scrapeSaga("TF", null);
            assertNull(result);
        }
    }

    // ── findBookUrls: href not starting with "/" → L196 ──────────────────────

    @Test
    void findBookUrls_absoluteHref_triggersLine196() throws Exception {
        when(repo.findByBookTitleIgnoreCase("TA")).thenReturn(Optional.empty());

        // Absolute URL (not filtered, does not start with "/") → L196
        String searchHtml = "<html><body><table class='tableList'>"
                + "<tr itemscope><td><a class='bookTitle' href='https://www.goodreads.com/book/show/456'>TA</a></td></tr>"
                + "</table></body></html>";
        String bookHtml = "<html><body><p>No series</p></body></html>";

        Document searchDoc = Jsoup.parse(searchHtml);
        Document bookDoc = Jsoup.parse(bookHtml);

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenReturn(searchDoc).thenReturn(bookDoc);

            SagaScrapedDto result = svc.scrapeSaga("TA", null);
            assertNull(result);
        }
    }

    // ── scrapeSeriesPage: empty books after filtering → returns null (L266-267) ──

    @Test
    void scrapeSaga_emptyBooksInSeries_returnsNull() throws Exception {
        when(repo.findByBookTitleIgnoreCase("TB")).thenReturn(Optional.empty());

        String searchHtml = "<html><body><table class='tableList'>"
                + "<tr itemscope><td><a class='bookTitle' href='/book/show/789'>TB</a></td></tr>"
                + "</table></body></html>";
        String bookHtml = "<html><body><a href='/series/789-emptysaga'>EmptySaga</a></body></html>";
        // Series page: has recognizable saga name but no books → books.isEmpty() → L266-267
        String seriesHtml = "<html><head><title>EmptySaga Series | Goodreads</title></head><body>"
                + "<div class='responsiveSeriesHeader__title'><h1>EmptySaga Series</h1></div>"
                + "</body></html>";

        Document searchDoc = Jsoup.parse(searchHtml);
        Document bookDoc = Jsoup.parse(bookHtml);
        Document seriesDoc = Jsoup.parse(seriesHtml);

        try (MockedStatic<Jsoup> mockedJsoup = mockStatic(Jsoup.class, CALLS_REAL_METHODS)) {
            Connection conn = mock(Connection.class);
            mockedJsoup.when(() -> Jsoup.connect(anyString())).thenReturn(conn);
            when(conn.userAgent(anyString())).thenReturn(conn);
            when(conn.timeout(anyInt())).thenReturn(conn);
            when(conn.get()).thenReturn(searchDoc).thenReturn(bookDoc).thenReturn(seriesDoc);

            SagaScrapedDto result = svc.scrapeSaga("TB", null);
            assertNull(result); // lines 266-267 triggered
        }
    }

    // ── extractBooksFromJson: non-numeric publicationDate → NumberFormatException (L373) ──

    @Test
    @SuppressWarnings("unchecked")
    void extractBooksFromJson_invalidPublicationDate_triggersLine373() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractBooksFromJson",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        String jsonProps = "{\"series\":[{\"book\":{"
                + "\"bookTitleBare\":\"My Title\","
                + "\"title\":\"My Title (Series #1)\","
                + "\"publicationDate\":\"not-a-year\","
                + "\"bookUrl\":\"/book/123\"}}]}";
        String html = "<html><body>"
                + "<div data-react-class=\"ReactComponents.SeriesList\" data-react-props='"
                + jsonProps + "'></div></body></html>";

        Document doc = Jsoup.parse(html);
        List<SagaScrapedDto.SagaBookEntry> books =
                (List<SagaScrapedDto.SagaBookEntry>) m.invoke(svc, doc);

        // Book is still added even though year could not be parsed
        assertFalse(books.isEmpty());
        assertEquals("My Title", books.get(0).getTitle());
    }

    // ── extractBooksFromJson: invalid JSON → Exception (L384-385) ─────────────

    @Test
    @SuppressWarnings("unchecked")
    void extractBooksFromJson_invalidJson_triggersLine384385() throws Exception {
        Method m = SagaScrapingService.class.getDeclaredMethod("extractBooksFromJson",
                org.jsoup.nodes.Document.class);
        m.setAccessible(true);

        // data-react-props contains invalid JSON → objectMapper.readTree throws →
        // caught by catch (Exception e) at L384
        String html = "<html><body>"
                + "<div data-react-class=\"ReactComponents.SeriesList\""
                + " data-react-props='{not: valid json}'>"
                + "</div></body></html>";

        Document doc = Jsoup.parse(html);
        List<?> result = (List<?>) m.invoke(svc, doc);
        assertTrue(result.isEmpty()); // exception swallowed, returns empty list
    }
}
