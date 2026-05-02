package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.lunaris_backend.domain.dto.NewsRequestDto;
import com.tfg.lunaris_backend.domain.model.News;
import com.tfg.lunaris_backend.domain.service.NewsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controlador que gestiona las operaciones sobre noticias.
 */
@RestController
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping("/news")
    public List<News> getAll() {
        return newsService.getAll();
    }

    @GetMapping("/news/{id}")
    public News getById(@PathVariable Long id) {
        News news = newsService.getById(id);
        if (news == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Noticia no encontrada");
        return news;
    }

    @PostMapping("/news")
    public News create(@RequestBody NewsRequestDto dto, Authentication auth) {
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo los administradores pueden crear noticias");

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setText(dto.getText());
        news.setBody(dto.getBody());
        news.setImage(dto.getImage());
        news.setDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")));
        return newsService.create(news);
    }

    @DeleteMapping("/news/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        if (auth == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!isAdmin)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Solo los administradores pueden eliminar noticias");

        if (newsService.getById(id) == null)
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Noticia no encontrada");
        newsService.delete(id);
    }
}
