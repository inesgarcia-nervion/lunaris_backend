package com.tfg.lunaris_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
