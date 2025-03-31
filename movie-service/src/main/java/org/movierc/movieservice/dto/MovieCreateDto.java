package org.movierc.movieservice.dto;

import jakarta.validation.Valid;
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
@Valid
public class MovieCreateDto {
    private String title;
    private String description;
    @Valid
    private List<String> images;
    @Valid
    private List<UUID> genres;
    @Valid
    private List<UUID> cast;
    private UUID director;
    private UUID screenwriter;
    private UUID cinematographer;
    private UUID composer;
    private UUID sourceMaterialAuthor;
}