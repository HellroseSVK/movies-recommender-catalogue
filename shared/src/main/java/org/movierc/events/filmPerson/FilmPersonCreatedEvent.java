package org.movierc.events.filmPerson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmPersonCreatedEvent {
    private UUID id;
    private String name;
    private URL image;
    private LocalDate dateOfBirth;
    private String biography;
}
