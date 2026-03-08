package com.tfg.lunaris_backend.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class BookCreateRequest {
    private String title;
    private String coverImage;
    private String description;
    private String author;
    private String apiId;
    private Integer releaseYear;
    private Double score;
    private String source;
    private Long userId;
    private List<Long> genreIds;
}
