package org.movierc.events.filmPerson;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmPersonUpdatedEvent {
    private UUID id;
    private String name;
    private String image;
    private LocalDate dateOfBirth;
    private String biography;

    private List<FilmPersonMovie> moviesAsActor;
    private List<FilmPersonMovie> moviesAsDirector;
    private List<FilmPersonMovie> moviesAsScreenwriter;
    private List<FilmPersonMovie> moviesAsCinematographer;
    private List<FilmPersonMovie> moviesAsComposer;
    private List<FilmPersonMovie> moviesAsSourceMaterial;
}
