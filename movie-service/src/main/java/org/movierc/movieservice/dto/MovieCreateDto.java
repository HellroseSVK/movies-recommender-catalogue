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
public class MovieCreateDto {
    private String title;
    private String description;
    private List<String> images;
    private List<UUID> genres;
    private List<UUID> cast;
    private UUID director;
    private UUID screenwriter;
    private UUID cinematographer;
    private UUID composer;
    private UUID sourceMaterialAuthor;
}