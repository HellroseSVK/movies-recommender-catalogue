package org.movierc.events.movie;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MovieGenre implements Serializable {
    private UUID id;
    private String name;
}
