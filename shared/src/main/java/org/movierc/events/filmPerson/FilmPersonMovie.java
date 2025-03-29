package org.movierc.events.filmPerson;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class FilmPersonMovie {
    private UUID id;
    private String title;
}
