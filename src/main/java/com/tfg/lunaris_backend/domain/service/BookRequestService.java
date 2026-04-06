package com.tfg.lunaris_backend.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tfg.lunaris_backend.data.repository.BookRequestRepository;
import com.tfg.lunaris_backend.domain.model.BookRequest;

import java.util.List;

/**
 * Servicio que maneja la lógica de negocio relacionada con las solicitudes de libros.
 * 
 * Proporciona métodos para obtener, crear y eliminar solicitudes de libros.
 */
@Service
public class BookRequestService {

    @Autowired
    private BookRequestRepository bookRequestRepository;

    /**
     * Obtiene una lista de todas las solicitudes de libros.
     * @return lista de solicitudes de libros
     */
    public List<BookRequest> getAll() {
        return bookRequestRepository.findAll();
    }

    /**
    * Obtiene una solicitud de libro por su identificador.
    * @param id identificador de la solicitud de libro
    * @return solicitud de libro encontrada o null si no se encuentra
    */
    public BookRequest getById(Long id) {
        return bookRequestRepository.findById(id).orElse(null);
    }

    /**
     * Crea una nueva solicitud de libro.
     * @param br solicitud de libro a crear
     * @return solicitud de libro creada
     */
    public BookRequest create(BookRequest br) {
        return bookRequestRepository.save(br);
    }

    /**
     * Elimina una solicitud de libro por su identificador.
     * @param id identificador de la solicitud de libro a eliminar
     */
    public void delete(Long id) {
        bookRequestRepository.deleteById(id);
    }
}
