package com.tfg.lunaris_backend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SagaScrapedDto {

    private String sagaName;
    private List<SagaBookEntry> books;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SagaBookEntry {
        private String title;
        private String author;
        private String orderNumber;
        private Integer pages;
        private Integer year;
        private String storygraphUrl;
    }
}
