package org.movierc.events.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieFilmPersonRoleAddedEvent implements Serializable {
    private MovieFilmPersonRole addedMovieFilmPersonRole;
}
