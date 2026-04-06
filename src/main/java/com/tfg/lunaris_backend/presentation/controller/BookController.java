package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.domain.dto.BookCreateRequest;
import com.tfg.lunaris_backend.domain.dto.OpenLibraryBookDto;
import com.tfg.lunaris_backend.domain.model.Book;
import com.tfg.lunaris_backend.domain.service.BookService;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Controlador que maneja las operaciones relacionadas con los libros.
 * 
 * Proporciona endpoints para crear, actualizar, eliminar y obtener libros.
 */
@RestController
public class BookController {
    @Autowired
    private BookService bookService;

    /**
     * Endpoint para obtener una lista de todos los libros de manera paginada.
     * @param pageable información de paginación
     * @return página de libros
     */
    @GetMapping("/books")
    public Page<Book> getAllBooks(Pageable pageable) {
        return bookService.getAllBooks(pageable);
    }

    /**
     * Endpoint para obtener un libro por su ID.
     * @param id identificador del libro
     * @return libro encontrado
     */
    @GetMapping("/books/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    /**
     * Endpoint para crear un nuevo libro. Recibe un objeto `BookCreateRequest` con 
     * los datos necesarios para crear el libro, y devuelve el libro creado.
     * @param request objeto con los datos para crear el libro
     * @return libro creado
     */ 
    @PostMapping("/books")
    public Book createBook(@RequestBody BookCreateRequest request) {
        return bookService.createBook(request);
    }

    /**
     * Endpoint para actualizar un libro existente. Recibe el ID del libro a 
     * actualizar y un objeto `Book` con los detalles a actualizar.
     * @param id identificador del libro a actualizar
     * @param bookDetails detalles del libro a actualizar
     * @return libro actualizado
     */
    @PutMapping("/books/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookService.updateBook(id, bookDetails);
    }

    /**
     * Endpoint para eliminar un libro por su ID.
     * @param id identificador del libro a eliminar
     */
    @DeleteMapping("/books/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    /**
     * Endpoint para buscar libros por título o autor. Recibe un parámetro de consulta 
     * `q` con el texto a buscar, y devuelve una lista de libros que coinciden con el título o autor.
     * @param query texto a buscar en el título o autor
     * @return lista de libros que coinciden con el criterio de búsqueda
     */
    @GetMapping("/books/search")
    public List<Book> searchBooks(@RequestParam("q") String query) {
        return bookService.searchBooks(query);
    }

    /**
     * Endpoint para obtener un libro por su ID de API. Recibe un parámetro de consulta 
     * `apiId` con el ID del libro en la API externa, y devuelve el libro encontrado o un 
     * estado 404 si no se encuentra.
     * @param apiId ID del libro en la API externa
     * @return libro encontrado o estado 404
     */
    @GetMapping("/books/by-api-id")
    public org.springframework.http.ResponseEntity<Book> getByApiId(@RequestParam("apiId") String apiId) {
        return bookService.findByApiId(apiId)
                .map(org.springframework.http.ResponseEntity::ok)
                .orElse(org.springframework.http.ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para importar un libro desde la API de Open Library. Recibe un objeto `OpenLibraryBookDto` 
     * con los datos del libro obtenidos de Open Library, y devuelve el libro creado o actualizado en la base de datos.
     * @param openLibraryBook objeto con los datos del libro de Open Library
     * @return libro creado o actualizado
     */
    @PostMapping("/books/import/openlibrary")
    public Book importFromOpenLibrary(@RequestBody OpenLibraryBookDto openLibraryBook) {
        return bookService.importFromOpenLibrary(openLibraryBook);
    }

}
