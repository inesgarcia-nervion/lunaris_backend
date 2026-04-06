package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.domain.dto.OpenLibrarySearchResponseDto;
import com.tfg.lunaris_backend.domain.service.OpenLibraryService;

/**
 * Controlador que maneja las operaciones relacionadas con la búsqueda de libros en Open Library.
 * 
 * Proporciona endpoints para buscar libros por título, autor o de manera general.
 */
@RestController
@RequestMapping("/api/openlibrary")
public class OpenLibraryController {

    @Autowired
    private OpenLibraryService openLibraryService;

    /**
     * Endpoint para buscar libros en Open Library. Recibe un parámetro de consulta `q` con el 
     * texto a buscar, y opcionalmente `limit` y `offset` para paginación. Devuelve una respuesta
     * con los resultados de la búsqueda.
     * @param query texto a buscar
     * @param limit número máximo de resultados a devolver
     * @param offset desplazamiento para la paginación
     * @return respuesta con los resultados de la búsqueda
     */
    @GetMapping("/search")
    public OpenLibrarySearchResponseDto search(
            @RequestParam("q") String query,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset) {
        return openLibraryService.searchBooks(query, limit, offset);
    }

    /**
     * Endpoint para buscar libros por título en Open Library. Recibe un parámetro de consulta `title` con el
     * texto a buscar en el título, y opcionalmente `limit` y `offset` para paginación. Devuelve una respuesta
     * con los resultados de la búsqueda.
     * @param title texto a buscar en el título
     * @param limit número máximo de resultados a devolver
     * @param offset desplazamiento para la paginación
     * @return respuesta con los resultados de la búsqueda por título
     */
    @GetMapping("/search/title")
    public OpenLibrarySearchResponseDto searchByTitle(
            @RequestParam("title") String title,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset) {
        return openLibraryService.searchByTitle(title, limit, offset);
    }

    /**
     * Endpoint para buscar libros por autor en Open Library. Recibe un parámetro de consulta `author` con el
     * texto a buscar en el autor, y opcionalmente `limit` y `offset` para paginación. Devuelve una respuesta
     * con los resultados de la búsqueda.
     * @param author texto a buscar en el autor
     * @param limit número máximo de resultados a devolver
     * @param offset desplazamiento para la paginación
     * @return respuesta con los resultados de la búsqueda por autor
     */
    @GetMapping("/search/author")
    public OpenLibrarySearchResponseDto searchByAuthor(
            @RequestParam("author") String author,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset) {
        return openLibraryService.searchByAuthor(author, limit, offset);
    }
}
