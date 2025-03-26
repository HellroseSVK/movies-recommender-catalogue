package org.movierc.movieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.movierc.movieservice.entity.Genre;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieUpdateDto {
    private String title;
    private String description;
    private List<String> images;
    private List<Genre> genres;
}