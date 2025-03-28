package org.movierc.movieservice.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.movierc.movieservice.entity.Genre;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class MovieUpdateDto {
    private String title;
    private String description;
    @Valid
    private List<String> images;
}