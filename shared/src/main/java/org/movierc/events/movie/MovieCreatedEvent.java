package org.movierc.events.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieCreatedEvent implements Serializable {
    private UUID id;
    private String title;
    private String description;
    private List<String> images;
    private List<MovieGenre> genres;

    private List<MovieFilmPersonRole> filmPersonRoles;
}
