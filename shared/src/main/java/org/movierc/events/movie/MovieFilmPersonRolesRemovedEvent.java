package org.movierc.events.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieFilmPersonRolesRemovedEvent implements Serializable {
    private List<MovieFilmPersonRole> removedMovieFilmPersonRoles;
}
