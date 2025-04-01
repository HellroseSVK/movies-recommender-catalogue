package org.movierc.movieservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private UUID id;
    private String title;
    private String description;
    private List<URL> images;
    private List<GenreDto> genres;
    private List<MovieFilmPersonRoleDto> filmPersonRoles;
    private float rating;
}