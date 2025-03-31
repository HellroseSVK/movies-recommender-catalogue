package org.movierc.movieservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private UUID id;
    private String title;
    private String description;
    private List<String> images;
    private List<Genre> genres;

    private List<FilmPerson> cast;
    private FilmPerson director;
    private FilmPerson screenwriter;
    private FilmPerson cinematographer;
    private FilmPerson composer;
    private FilmPerson sourceMaterialAuthor;

    private final Map<UUID, Integer> totalRatings = new HashMap<>();
    private final Map<UUID, Integer> numbersOfRatings = new HashMap<>();

    public float getRating() {
        int totalRating = totalRatings.values().stream().mapToInt(Integer::intValue).sum();
        int numberOfRatings = numbersOfRatings.values().stream().mapToInt(Integer::intValue).sum();

        if (numberOfRatings == 0) {
            return 0;
        }
        return (float) totalRating / numberOfRatings;
    }
}
