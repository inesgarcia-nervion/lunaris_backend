package com.tfg.lunaris_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.lunaris_backend.dto.OpenLibrarySearchResponseDto;
import com.tfg.lunaris_backend.service.OpenLibraryService;

@RestController
@RequestMapping("/api/openlibrary")
public class OpenLibraryController {

    @Autowired
    private OpenLibraryService openLibraryService;

    // Búsqueda general de libros en Open Library
    @GetMapping("/search")
    public OpenLibrarySearchResponseDto search(
            @RequestParam("q") String query,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset) {
        return openLibraryService.searchBooks(query, limit, offset);
    }

    // Búsqueda de libros por título
    @GetMapping("/search/title")
    public OpenLibrarySearchResponseDto searchByTitle(
            @RequestParam("title") String title,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset) {
        return openLibraryService.searchByTitle(title, limit, offset);
    }

    // Búsqueda de libros por autor
    @GetMapping("/search/author")
    public OpenLibrarySearchResponseDto searchByAuthor(
            @RequestParam("author") String author,
            @RequestParam(value = "limit", required = false) Integer limit,
            @RequestParam(value = "offset", required = false) Integer offset) {
        return openLibraryService.searchByAuthor(author, limit, offset);
    }
}
