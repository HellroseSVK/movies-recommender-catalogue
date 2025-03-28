package org.movierc.events.movie;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class MovieFilmPerson {
    private UUID id;
    private String name;
}
