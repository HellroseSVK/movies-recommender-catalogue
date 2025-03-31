package org.movierc.movieservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private UUID id;
    private String title;
    private String description;
    private List<String> images;
    private List<GenreDto> genres;
    private List<FilmPersonDto> cast;
    private FilmPersonDto director;
    private FilmPersonDto screenwriter;
    private FilmPersonDto cinematographer;
    private FilmPersonDto composer;
    private FilmPersonDto sourceMaterialAuthor;
    private float rating;
}