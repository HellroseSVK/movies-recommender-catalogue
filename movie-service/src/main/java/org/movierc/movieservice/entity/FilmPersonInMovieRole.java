package org.movierc.movieservice.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.movierc.shared.enm.FilmPersonRole;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmPersonInMovieRole {
    private Movie movie;
    private FilmPerson filmPerson;
    private FilmPersonRole role;
}
