package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.BookRequestRepository;
import com.tfg.lunaris_backend.domain.model.BookRequest;

import java.util.List;

@Service
public class BookRequestService {

    @Autowired
    private BookRequestRepository bookRequestRepository;

    public List<BookRequest> getAll() {
        return bookRequestRepository.findAll();
    }

    public BookRequest getById(Long id) {
        return bookRequestRepository.findById(id).orElse(null);
    }

    public BookRequest create(BookRequest br) {
        return bookRequestRepository.save(br);
    }

    public void delete(Long id) {
        bookRequestRepository.deleteById(id);
    }
}
