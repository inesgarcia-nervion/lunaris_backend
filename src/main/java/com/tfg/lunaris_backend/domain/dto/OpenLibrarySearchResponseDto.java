package com.tfg.lunaris_backend.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO que representa la respuesta de una búsqueda en OpenLibrary.
 * 
 * Contiene el número total de resultados encontrados, el índice de inicio de los resultados actuales 
 * y una lista de libros (OpenLibraryBookDto) que coinciden con la búsqueda realizada.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibrarySearchResponseDto {

    @JsonProperty("numFound")
    private Integer numFound;

    @JsonProperty("start")
    private Integer start;

    @JsonProperty("docs")
    private List<OpenLibraryBookDto> docs;

    public boolean hasResults() {
        return docs != null && !docs.isEmpty();
    }
}
