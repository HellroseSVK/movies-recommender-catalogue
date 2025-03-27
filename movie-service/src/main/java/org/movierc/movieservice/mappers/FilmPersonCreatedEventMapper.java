package org.movierc.movieservice.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.entity.Movie;

import java.util.UUID;

@Mapper
public interface FilmPersonCreatedEventMapper {
    FilmPerson filmPersonCreatedEventToFilmPerson(FilmPersonCreatedEvent filmPersonCreatedEvent);
}
