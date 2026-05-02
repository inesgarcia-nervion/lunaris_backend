package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;

/**
 * DTO para actualizar el estado de lectura de un libro para un usuario.
 *
 * bookId corresponde al campo key de OpenLibrary (ej: /works/OL12345W).
 * status puede ser "Plan para leer", "Leyendo", "Leído", o null/vacío para
 * eliminar el estado.
 * bookData contiene el objeto libro serializado como JSON para su
 * almacenamiento.
 */
@Data
public class BookStatusRequest {
    private String bookId;
    private String status;
    private Object bookData;
}
