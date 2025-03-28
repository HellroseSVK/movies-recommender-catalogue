package org.movierc.movieservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
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

    private float totalRating;
    private float numberOfRatings;

    public float getRating() {
        if (numberOfRatings == 0) {
            return 0;
        }
        return totalRating / numberOfRatings;
    }
}
