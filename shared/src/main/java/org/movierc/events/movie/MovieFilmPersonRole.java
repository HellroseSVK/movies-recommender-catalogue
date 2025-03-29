package org.movierc.events.movie;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.movierc.shared.enm.FilmPersonRole;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MovieFilmPersonRole implements Serializable {
    private UUID movieId;
    private UUID filmPersonId;
    private FilmPersonRole role;
}
