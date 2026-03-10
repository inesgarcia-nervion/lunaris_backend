package com.tfg.lunaris_backend.presentation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.tfg.lunaris_backend.domain.model.BookRequest;
import com.tfg.lunaris_backend.domain.service.BookRequestService;

import java.util.List;

@RestController
public class BookRequestController {
    @Autowired
    private BookRequestService bookRequestService;

    @GetMapping("/requests")
    public List<BookRequest> getAll() {
        return bookRequestService.getAll();
    }

    @PostMapping("/requests")
    public BookRequest create(@RequestBody BookRequest br) {
        return bookRequestService.create(br);
    }

    @DeleteMapping("/requests/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {
        boolean isAdmin = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

        if (!isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No autorizado");
        }

        bookRequestService.delete(id);
    }
}
