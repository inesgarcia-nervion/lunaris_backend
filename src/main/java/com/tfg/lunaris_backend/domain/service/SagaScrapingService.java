package com.tfg.lunaris_backend.domain.service;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import com.tfg.lunaris_backend.data.repository.SagaRepository;
import com.tfg.lunaris_backend.domain.dto.SagaScrapedDto;
import com.tfg.lunaris_backend.domain.dto.SagaScrapedDto.SagaBookEntry;
import com.tfg.lunaris_backend.domain.model.Saga;
import com.tfg.lunaris_backend.domain.model.SagaBook;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class SagaScrapingService {

    @Autowired
    private SagaRepository sagaRepository;

    private static final String GOODREADS_BASE = "https://www.goodreads.com";
    private static final String GOODREADS_SEARCH_URL = GOODREADS_BASE + "/search?q=";
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";
    private static final int TIMEOUT_MS = 10000;
    private static final Pattern SERIES_NUMBER_PATTERN = Pattern.compile("#([\\d.]+(?:-[\\d.]+)?)\\)");
    private static final Pattern SERIES_URL_PATTERN = Pattern.compile("/series/(\\d+)");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Busca la saga/serie de un libro a partir de su título y autor.
     * Scrapea Goodreads para obtener la lista completa de libros de la saga.
     *
     * @param bookTitle título del libro
     * @param author    autor del libro (puede ser null)
     * @return DTO con el nombre de la saga y sus libros, o null si no pertenece a
     *         una saga
     */
    public SagaScrapedDto scrapeSaga(String bookTitle, String author) {
        // Comprobar caché en base de datos primero
        Optional<Saga> cachedSaga = sagaRepository.findByBookTitleIgnoreCase(bookTitle);
        if (cachedSaga.isPresent()) {
            Saga saga = cachedSaga.get();
            // Verificar que la caché no contiene entradas sin filtrar
            boolean hasUnfiltered = saga.getBooks().stream().anyMatch(sb -> sb.getOrderNumber() == null
                    || sb.getOrderNumber().isBlank() || sb.getOrderNumber().contains("-"));
            if (!hasUnfiltered && saga.getBooks().size() > 2) {
                log.info("Saga encontrada en caché para '{}'", bookTitle);
                return convertToDto(saga);
            }
            // Caché obsoleta: eliminar para re-scrapear
            sagaRepository.delete(saga);
            log.info("Caché obsoleta de saga '{}' eliminada, re-scrapeando...", saga.getName());
        }

        try {
            // Paso 1: Buscar el libro en Goodreads y obtener URLs candidatas
            List<String> bookUrls = findBookUrls(bookTitle);
            if (bookUrls.isEmpty() && author != null && !author.isBlank()) {
                // Reintentar sólo con el título si no hubo resultados
                bookUrls = findBookUrls(bookTitle);
            }
            if (bookUrls.isEmpty()) {
                log.info("No se encontró el libro '{}' en Goodreads", bookTitle);
                return null;
            }

            // Paso 2: Probar cada resultado hasta encontrar uno con enlace a serie
            for (String bookUrl : bookUrls) {
                String seriesUrl = findSeriesUrl(bookUrl);
                if (seriesUrl != null) {
                    // Paso 3: Scrapear la página de la serie
                    SagaScrapedDto result = scrapeSeriesPage(seriesUrl);
                    if (result != null) {
                        // Guardar en base de datos para futuras consultas
                        saveSagaToDb(result);
                        return result;
                    }
                }
            }

            log.info("El libro '{}' no pertenece a ninguna saga", bookTitle);
            return null;

        } catch (IOException e) {
            log.error("Error al scrapear saga para '{}': {}", bookTitle, e.getMessage());
            return null;
        }
    }

    /**
     * Convierte una entidad Saga de la base de datos a SagaScrapedDto.
     */
    private SagaScrapedDto convertToDto(Saga saga) {
        List<SagaBookEntry> entries = new ArrayList<>();
        for (SagaBook sb : saga.getBooks()) {
            SagaBookEntry entry = new SagaBookEntry();
            entry.setTitle(sb.getTitle());
            entry.setAuthor(sb.getAuthor());
            entry.setOrderNumber(sb.getOrderNumber());
            entry.setPages(sb.getPages());
            entry.setYear(sb.getYear());
            entry.setStorygraphUrl(sb.getGoodreadsUrl());
            entries.add(entry);
        }
        return new SagaScrapedDto(saga.getName(), entries);
    }

    /**
     * Guarda la saga scrapeada en la base de datos.
     * Si ya existe y tiene menos libros que los scrapeados, la actualiza.
     */
    private void saveSagaToDb(SagaScrapedDto dto) {
        Optional<Saga> existing = sagaRepository.findByName(dto.getSagaName());
        if (existing.isPresent()) {
            Saga saga = existing.get();
            if (saga.getBooks().size() >= dto.getBooks().size()) {
                return; // La caché ya tiene todos los libros
            }
            // Actualizar con datos más completos
            saga.getBooks().clear();
            for (SagaBookEntry entry : dto.getBooks()) {
                SagaBook sb = new SagaBook();
                sb.setTitle(entry.getTitle());
                sb.setAuthor(entry.getAuthor());
                sb.setOrderNumber(entry.getOrderNumber());
                sb.setPages(entry.getPages());
                sb.setYear(entry.getYear());
                sb.setGoodreadsUrl(entry.getStorygraphUrl());
                sb.setSaga(saga);
                saga.getBooks().add(sb);
            }
            sagaRepository.save(saga);
            log.info("Saga '{}' actualizada en base de datos con {} libros", dto.getSagaName(), saga.getBooks().size());
            return;
        }

        Saga saga = new Saga();
        saga.setName(dto.getSagaName());

        for (SagaBookEntry entry : dto.getBooks()) {
            SagaBook sb = new SagaBook();
            sb.setTitle(entry.getTitle());
            sb.setAuthor(entry.getAuthor());
            sb.setOrderNumber(entry.getOrderNumber());
            sb.setPages(entry.getPages());
            sb.setYear(entry.getYear());
            sb.setGoodreadsUrl(entry.getStorygraphUrl());
            sb.setSaga(saga);
            saga.getBooks().add(sb);
        }

        sagaRepository.save(saga);
        log.info("Saga '{}' guardada en base de datos con {} libros", dto.getSagaName(), saga.getBooks().size());
    }

    /**
     * Busca un libro en Goodreads y devuelve las URLs de los primeros resultados.
     * Busca sólo por título para evitar que "guides" y "summaries" del autor
     * desplacen al libro real.
     */
    private List<String> findBookUrls(String bookTitle) throws IOException {
        String searchUrl = GOODREADS_SEARCH_URL + URLEncoder.encode(bookTitle, StandardCharsets.UTF_8);

        log.debug("Buscando en Goodreads: {}", searchUrl);

        Document doc = Jsoup.connect(searchUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get();

        // Los resultados están en una tabla con clase "tableList"
        Elements results = doc.select("table.tableList tr[itemscope] a.bookTitle");
        List<String> urls = new ArrayList<>();
        int limit = Math.min(results.size(), 5); // Probar hasta 5 resultados

        for (int i = 0; i < limit; i++) {
            String href = results.get(i).attr("href");
            // Filtrar resultados que son claramente guides/summaries
            String hrefLower = href.toLowerCase();
            if (hrefLower.contains("summary") || hrefLower.contains("study-guide")
                    || hrefLower.contains("book-analysis") || hrefLower.contains("companion")
                    || hrefLower.contains("unofficial")) {
                continue;
            }
            if (href.startsWith("/")) {
                urls.add(GOODREADS_BASE + href);
            } else {
                urls.add(href);
            }
        }

        // Si todos fueron filtrados, incluir el primero original como fallback
        if (urls.isEmpty() && !results.isEmpty()) {
            String href = results.first().attr("href");
            urls.add(href.startsWith("/") ? GOODREADS_BASE + href : href);
        }

        return urls;
    }

    /**
     * Obtiene la URL de la serie desde la página de detalle de un libro en
     * Goodreads.
     */
    private String findSeriesUrl(String bookUrl) throws IOException {
        log.debug("Obteniendo página del libro: {}", bookUrl);

        Document doc = Jsoup.connect(bookUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get();

        // La serie aparece en un elemento con aria-label que contiene "in the ...
        // series"
        // dentro de BookPageTitleSection: <a href="/series/ID-name">SeriesName #N</a>
        Element seriesLink = doc.selectFirst("h3.Text a[href*='/series/']");
        if (seriesLink == null) {
            // Intentar selector alternativo
            seriesLink = doc.selectFirst("a[href*='/series/']");
        }
        if (seriesLink == null) {
            return null;
        }

        String href = seriesLink.attr("href");
        if (!href.startsWith("http")) {
            href = GOODREADS_BASE + href;
        }
        return href;
    }

    /**
     * Scrapea la página de una serie en Goodreads y extrae todos los libros.
     * Los datos están embebidos como JSON en un atributo data-react-props.
     */
    private SagaScrapedDto scrapeSeriesPage(String seriesUrl) throws IOException {
        log.debug("Scrapeando serie: {}", seriesUrl);

        Document doc = Jsoup.connect(seriesUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get();

        // Obtener el nombre de la serie del encabezado
        String sagaName = extractSeriesName(doc);
        if (sagaName == null) {
            log.warn("No se pudo extraer el nombre de la serie de {}", seriesUrl);
            return null;
        }

        // Los datos de los libros están en un JSON embebido en data-react-props
        List<SagaBookEntry> books = extractBooksFromJson(doc);

        if (books.isEmpty()) {
            // Fallback: parsear el HTML directamente
            books = extractBooksFromHtml(doc);
        }

        // Filtrar: sin número, rangos (1-2), y duplicados
        books = filterSagaBooks(books);

        if (books.isEmpty()) {
            log.warn("No se encontraron libros en la serie '{}'", sagaName);
            return null;
        }

        return new SagaScrapedDto(sagaName, books);
    }

    /**
     * Filtra libros de la saga: elimina los que no tienen número de orden,
     * los que tienen rangos (e.g. "1-2", "1-3") y duplicados por número.
     */
    private List<SagaBookEntry> filterSagaBooks(List<SagaBookEntry> books) {
        java.util.LinkedHashSet<String> seenNumbers = new java.util.LinkedHashSet<>();
        List<SagaBookEntry> filtered = new ArrayList<>();

        for (SagaBookEntry book : books) {
            String order = book.getOrderNumber();
            // Sin número de orden → descartar
            if (order == null || order.isBlank()) {
                continue;
            }
            // Rangos como "1-2", "1-3", "1-4" → compilaciones/box sets → descartar
            if (order.contains("-")) {
                continue;
            }
            // Número duplicado → descartar
            if (!seenNumbers.add(order)) {
                continue;
            }
            filtered.add(book);
        }
        return filtered;
    }

    /**
     * Extrae el nombre de la serie del encabezado de la página.
     */
    private String extractSeriesName(Document doc) {
        Element header = doc.selectFirst("div.responsiveSeriesHeader__title h1");
        if (header != null) {
            String name = header.text().trim();
            // Eliminar el sufijo " Series" si existe
            if (name.endsWith(" Series")) {
                name = name.substring(0, name.length() - 7).trim();
            }
            return name;
        }
        // Fallback: Extraer del <title>
        String title = doc.title();
        if (title != null && title.contains("Series")) {
            return title.replace("Series by", "").replace("| Goodreads", "").trim();
        }
        return null;
    }

    /**
     * Extrae los libros de la serie a partir del JSON embebido en data-react-props.
     * Goodreads puede dividir los libros en múltiples elementos SeriesList.
     */
    private List<SagaBookEntry> extractBooksFromJson(Document doc) {
        List<SagaBookEntry> books = new ArrayList<>();
        Elements reactElements = doc.select("[data-react-class='ReactComponents.SeriesList']");
        if (reactElements.isEmpty()) {
            return books;
        }

        for (Element reactElement : reactElements) {
            String jsonStr = reactElement.attr("data-react-props");
            if (jsonStr.isEmpty()) {
                continue;
            }

            try {
                JsonNode root = objectMapper.readTree(jsonStr);
                JsonNode seriesArray = root.get("series");
                if (seriesArray == null || !seriesArray.isArray()) {
                    continue;
                }

                for (JsonNode entry : seriesArray) {
                    JsonNode bookNode = entry.get("book");
                    if (bookNode == null) {
                        continue;
                    }

                    SagaBookEntry bookEntry = new SagaBookEntry();

                    // Título limpio (sin la parte de la serie entre paréntesis)
                    String titleBare = getTextNode(bookNode, "bookTitleBare");
                    bookEntry.setTitle(cleanHtmlEntities(titleBare));

                    // Autor
                    JsonNode authorNode = bookNode.get("author");
                    if (authorNode != null) {
                        bookEntry.setAuthor(getTextNode(authorNode, "name"));
                    }

                    // Número de orden: extraer del título completo, e.g. "(Harry Potter, #1)"
                    String fullTitle = getTextNode(bookNode, "title");
                    bookEntry.setOrderNumber(extractOrderNumber(fullTitle));

                    // Páginas
                    if (bookNode.has("numPages") && !bookNode.get("numPages").isNull()) {
                        bookEntry.setPages(bookNode.get("numPages").asInt());
                    }

                    // Año de publicación
                    String pubDate = getTextNode(bookNode, "publicationDate");
                    if (pubDate != null && !pubDate.isEmpty()) {
                        try {
                            bookEntry.setYear(Integer.parseInt(pubDate.trim()));
                        } catch (NumberFormatException ignored) {
                        }
                    }

                    // URL del libro en Goodreads
                    String bookUrlPath = getTextNode(bookNode, "bookUrl");
                    if (bookUrlPath != null) {
                        bookEntry.setStorygraphUrl(GOODREADS_BASE + bookUrlPath);
                    }

                    books.add(bookEntry);
                }
            } catch (Exception e) {
                log.error("Error parseando JSON de la serie: {}", e.getMessage());
            }
        }

        return books;
    }

    /**
     * Fallback: extrae libros parseando directamente el HTML de la página de
     * series.
     */
    private List<SagaBookEntry> extractBooksFromHtml(Document doc) {
        List<SagaBookEntry> books = new ArrayList<>();
        Elements items = doc.select("div.listWithDividers__item");

        for (Element item : items) {
            SagaBookEntry bookEntry = new SagaBookEntry();

            // Número de libro desde el encabezado h3 (e.g., "Book 1")
            Element numberHeader = item.selectFirst("h3");
            if (numberHeader != null) {
                String text = numberHeader.text().trim();
                Matcher m = Pattern.compile("\\d+(?:\\.\\d+)?").matcher(text);
                if (m.find()) {
                    bookEntry.setOrderNumber(m.group());
                }
            }

            // Título
            Element titleElement = item.selectFirst("span[itemprop=name]");
            if (titleElement != null) {
                bookEntry.setTitle(titleElement.text().trim());
            }

            // Autor
            Element authorElement = item.selectFirst("span[itemprop=author] span[itemprop=name]");
            if (authorElement != null) {
                bookEntry.setAuthor(authorElement.text().trim());
            }

            // URL
            Element linkElement = item.selectFirst("a[itemprop=url]");
            if (linkElement != null) {
                String href = linkElement.attr("href");
                bookEntry.setStorygraphUrl(href.startsWith("http") ? href : GOODREADS_BASE + href);
            }

            if (bookEntry.getTitle() != null) {
                books.add(bookEntry);
            }
        }

        return books;
    }

    /**
     * Extrae el número de orden del título completo, e.g. "(Series, #1)" → "1"
     */
    private String extractOrderNumber(String fullTitle) {
        if (fullTitle == null)
            return null;
        Matcher m = SERIES_NUMBER_PATTERN.matcher(fullTitle);
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    private String getTextNode(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull())
            return null;
        return value.asText();
    }

    private String cleanHtmlEntities(String text) {
        if (text == null)
            return null;
        return text.replace("&#39;", "'")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replace("&lt;", "<")
                .replace("&gt;", ">");
    }
}
