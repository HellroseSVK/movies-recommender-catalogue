package org.movierc.movieservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.movieservice.entity.FilmPerson;

@Mapper
public interface FilmPersonCreatedEventMapper {
    FilmPerson filmPersonCreatedEventToFilmPerson(FilmPersonCreatedEvent filmPersonCreatedEvent);
}
