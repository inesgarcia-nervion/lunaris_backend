package com.tfg.lunaris_backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryBookDto {

    @JsonProperty("key")
    private String key;

    @JsonProperty("title")
    private String title;

    @JsonProperty("author_name")
    private List<String> authorNames;

    @JsonProperty("first_publish_year")
    private Integer firstPublishYear;

    @JsonProperty("cover_i")
    private Integer coverId;

    @JsonProperty("edition_count")
    private Integer editionCount;

    @JsonProperty("ia")
    private List<String> internetArchiveIds;

    @JsonProperty("has_fulltext")
    private Boolean hasFulltext;

    @JsonProperty("description")
    private String description;

    @JsonProperty("ratings_average")
    private Double ratingsAverage;

    public String getFirstAuthor() {
        return (authorNames != null && !authorNames.isEmpty()) ? authorNames.get(0) : null;
    }

    public String getCoverUrl() {
        if (coverId != null) {
            return "https://covers.openlibrary.org/b/id/" + coverId + "-M.jpg";
        }
        return null;
    }
}
