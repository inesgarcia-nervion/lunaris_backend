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

/**
 * Servicio que maneja la lógica de negocio relacionada con el scraping de sagas
 * en Goodreads.
 * 
 * Proporciona métodos para buscar sagas y obtener información detallada de los
 * libros que las componen.
 */
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
        Optional<Saga> cachedSaga = sagaRepository.findByBookTitleIgnoreCase(bookTitle);
        if (cachedSaga.isPresent()) {
            Saga saga = cachedSaga.get();
            boolean hasGoodreadsUrls = saga.getBooks().stream()
                    .anyMatch(sb -> sb.getGoodreadsUrl() != null && !sb.getGoodreadsUrl().isBlank());
            if (!hasGoodreadsUrls) {
                log.info("Saga local encontrada para '{}' (manual)", bookTitle);
                return convertToDto(saga);
            }

            boolean hasUnfiltered = saga.getBooks().stream().anyMatch(sb -> sb.getOrderNumber() == null
                    || sb.getOrderNumber().isBlank() || sb.getOrderNumber().contains("-"));
            if (!hasUnfiltered && saga.getBooks().size() > 2) {
                log.info("Saga encontrada en caché para '{}'", bookTitle);
                return convertToDto(saga);
            }
            sagaRepository.delete(saga);
            log.info("Caché obsoleta de saga '{}' eliminada, re-scrapeando...", saga.getName());
        }

        try {
            List<String> bookUrls = findBookUrls(bookTitle);
            if (bookUrls.isEmpty() && author != null && !author.isBlank()) {
                String query = (bookTitle + " " + author).trim();
                bookUrls = findBookUrls(query);
            }
            if (bookUrls.isEmpty()) {
                log.info("No se encontró el libro '{}' en Goodreads", bookTitle);
                return null;
            }

            for (String bookUrl : bookUrls) {
                String seriesUrl = findSeriesUrl(bookUrl);
                if (seriesUrl != null) {
                    SagaScrapedDto result = scrapeSeriesPage(seriesUrl);
                    if (result != null) {
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
     * Convierte la entidad Saga a un DTO para la respuesta, extrayendo sólo los
     * campos necesarios.
     * 
     * @param saga entidad de la saga con sus libros
     * @return DTO con el nombre de la saga y una lista de libros con sus datos
     *         relevantes
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
     * Guarda la saga scrapeada en la base de datos. Si ya existe una saga con el
     * mismo nombre,
     * se actualiza sólo si la nueva tiene más libros o datos más completos.
     * 
     * @param dto DTO con el nombre de la saga y sus libros obtenidos del scraping
     */
    private void saveSagaToDb(SagaScrapedDto dto) {
        Optional<Saga> existing = sagaRepository.findByName(dto.getSagaName());
        if (existing.isPresent()) {
            Saga saga = existing.get();
            if (saga.getBooks().size() >= dto.getBooks().size()) {
                return;
            }
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
     * Busca en Goodreads la URL de la serie a la que pertenece un libro, a partir
     * de su título.
     * 
     * @param bookTitle título del libro a buscar
     * @return URL de la serie en Goodreads, o null si no se encuentra o no
     *         pertenece a ninguna serie
     * @throws IOException si hay un error al conectar o parsear la página de
     *                     Goodreads
     */
    private List<String> findBookUrls(String bookTitle) throws IOException {
        String searchUrl = GOODREADS_SEARCH_URL + URLEncoder.encode(bookTitle, StandardCharsets.UTF_8);

        log.debug("Buscando en Goodreads: {}", searchUrl);

        Document doc = Jsoup.connect(searchUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get();

        Elements results = doc.select("table.tableList tr[itemscope] a.bookTitle");
        List<String> urls = new ArrayList<>();
        int limit = Math.min(results.size(), 5);

        for (int i = 0; i < limit; i++) {
            String href = results.get(i).attr("href");
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

        if (urls.isEmpty() && !results.isEmpty()) {
            String href = results.first().attr("href");
            urls.add(href.startsWith("/") ? GOODREADS_BASE + href : href);
        }

        return urls;
    }

    /**
     * Dada la URL de un libro en Goodreads, obtiene la URL de la serie a la que
     * pertenece, si existe.
     * 
     * @param bookUrl URL del libro en Goodreads
     * @return URL de la serie en Goodreads, o null si no se encuentra o el libro no
     *         pertenece a ninguna serie
     * @throws IOException si hay un error al conectar o parsear la página del libro
     *                     en Goodreads
     */
    private String findSeriesUrl(String bookUrl) throws IOException {
        log.debug("Obteniendo página del libro: {}", bookUrl);

        Document doc = Jsoup.connect(bookUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get();
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
     * Scrapea la página de la serie en Goodreads para extraer el nombre de la saga
     * y la lista de libros con sus datos.
     * 
     * @param seriesUrl URL de la serie en Goodreads
     * @return DTO con el nombre de la saga y una lista de libros con sus datos
     *         relevantes, o null si no se pudo extraer la información
     * @throws IOException si hay un error al conectar o parsear la página de la
     *                     serie en Goodreads
     */
    private SagaScrapedDto scrapeSeriesPage(String seriesUrl) throws IOException {
        log.debug("Scrapeando serie: {}", seriesUrl);

        Document doc = Jsoup.connect(seriesUrl)
                .userAgent(USER_AGENT)
                .timeout(TIMEOUT_MS)
                .get();

        String sagaName = extractSeriesName(doc);
        if (sagaName == null) {
            log.warn("No se pudo extraer el nombre de la serie de {}", seriesUrl);
            return null;
        }

        List<SagaBookEntry> books = extractBooksFromJson(doc);

        if (books.isEmpty()) {
            books = extractBooksFromHtml(doc);
        }

        books = filterSagaBooks(books);

        if (books.isEmpty()) {
            log.warn("No se encontraron libros en la serie '{}'", sagaName);
            return null;
        }

        return new SagaScrapedDto(sagaName, books);
    }

    /**
     * Filtra la lista de libros de la saga para eliminar entradas sin número de
     * orden o con números de orden no válidos (rangos, duplicados).
     * 
     * @param books lista original de libros extraídos del scraping
     * @return lista filtrada de libros que tienen un número de orden válido y único
     *         dentro de la saga
     */
    private List<SagaBookEntry> filterSagaBooks(List<SagaBookEntry> books) {
        java.util.LinkedHashSet<String> seenNumbers = new java.util.LinkedHashSet<>();
        List<SagaBookEntry> filtered = new ArrayList<>();

        for (SagaBookEntry book : books) {
            String order = book.getOrderNumber();
            if (order == null || order.isBlank()) {
                continue;
            }
            if (order.contains("-")) {
                continue;
            }
            if (!seenNumbers.add(order)) {
                continue;
            }
            filtered.add(book);
        }
        return filtered;
    }

    /**
     * Extrae el nombre de la saga de la página de la serie en Goodreads, intentando
     * primero el
     * encabezado específico y luego el título de la página como fallback.
     * 
     * @param doc documento HTML de la página de la serie en Goodreads
     * @return nombre de la saga, o null si no se pudo extraer el nombre de la saga
     *         de la página
     */
    private String extractSeriesName(Document doc) {
        Element header = doc.selectFirst("div.responsiveSeriesHeader__title h1");
        if (header != null) {
            String name = header.text().trim();
            if (name.endsWith(" Series")) {
                name = name.substring(0, name.length() - 7).trim();
            }
            return name;
        }
        String title = doc.title();
        if (title != null && title.contains("Series")) {
            return title.replace("Series by", "").replace("| Goodreads", "").trim();
        }
        return null;
    }

    /**
     * Extrae la lista de libros de la saga a partir del JSON embebido en la página
     * de la serie
     * en Goodreads, que suele ser más estructurado y completo que el HTML.
     * 
     * @param doc documento HTML de la página de la serie en Goodreads
     * @return lista de libros extraída del JSON, o una lista vacía si no se pudo
     *         extraer o el JSON no estaba presente en la página
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

                    String titleBare = getTextNode(bookNode, "bookTitleBare");
                    bookEntry.setTitle(cleanHtmlEntities(titleBare));

                    JsonNode authorNode = bookNode.get("author");
                    if (authorNode != null) {
                        bookEntry.setAuthor(getTextNode(authorNode, "name"));
                    }

                    String fullTitle = getTextNode(bookNode, "title");
                    bookEntry.setOrderNumber(extractOrderNumber(fullTitle));

                    if (bookNode.has("numPages") && !bookNode.get("numPages").isNull()) {
                        bookEntry.setPages(bookNode.get("numPages").asInt());
                    }

                    String pubDate = getTextNode(bookNode, "publicationDate");
                    if (pubDate != null && !pubDate.isEmpty()) {
                        try {
                            bookEntry.setYear(Integer.parseInt(pubDate.trim()));
                        } catch (NumberFormatException ignored) {
                        }
                    }

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
     * Extrae la lista de libros de la saga a partir del HTML de la página de la
     * serie en Goodreads,
     * como fallback si el JSON no estaba presente o no se pudo parsear.
     * 
     * @param doc documento HTML de la página de la serie en Goodreads
     * @return lista de libros extraída del HTML, o una lista vacía si no se pudo
     *         extraer la
     *         información de los libros del HTML de la página de la serie en
     *         Goodreads
     */
    private List<SagaBookEntry> extractBooksFromHtml(Document doc) {
        List<SagaBookEntry> books = new ArrayList<>();
        Elements items = doc.select("div.listWithDividers__item");

        for (Element item : items) {
            SagaBookEntry bookEntry = new SagaBookEntry();

            Element numberHeader = item.selectFirst("h3");
            if (numberHeader != null) {
                String text = numberHeader.text().trim();
                Matcher m = Pattern.compile("\\d+(?:\\.\\d+)?").matcher(text);
                if (m.find()) {
                    bookEntry.setOrderNumber(m.group());
                }
            }

            Element titleElement = item.selectFirst("span[itemprop=name]");
            if (titleElement != null) {
                bookEntry.setTitle(titleElement.text().trim());
            }

            Element authorElement = item.selectFirst("span[itemprop=author] span[itemprop=name]");
            if (authorElement != null) {
                bookEntry.setAuthor(authorElement.text().trim());
            }

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
     * Extrae el número de orden del libro dentro de la saga a partir del título
     * completo, buscando patrones como "#1", "#2.5", "#1-3", etc.
     * 
     * @param fullTitle título completo del libro que puede contener el número
     *                  de orden dentro de la saga
     * @return número de orden extraído del título, o null si no se pudo encontrar
     *         un número de orden válido en el título del libro dentro de la saga
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

    /**
     * Extrae el texto de un nodo JSON, devolviendo null si el nodo no existe o es
     * nulo, y limpiando entidades HTML comunes.
     * 
     * @param node  nodo JSON del que extraer el texto
     * @param field nombre del campo a extraer del nodo JSON
     * @return texto del campo extraído y limpiado de entidades HTML, o null si el
     *         campo no existe o es nulo en el nodo JSON
     */
    private String getTextNode(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.isNull())
            return null;
        return value.asText();
    }

    /**
     * Limpia entidades HTML comunes de un texto, como &#39; &amp; &quot; &lt; &gt;,
     * para obtener un texto legible.
     * 
     * @param text texto que puede contener entidades HTML
     * @return texto con las entidades HTML comunes reemplazadas por sus caracteres
     *         correspondientes, o null si el texto de entrada es null
     */
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
