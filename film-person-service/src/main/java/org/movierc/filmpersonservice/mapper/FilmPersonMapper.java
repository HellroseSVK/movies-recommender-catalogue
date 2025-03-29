package org.movierc.filmpersonservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.events.filmPerson.FilmPersonDeletedEvent;
import org.movierc.events.filmPerson.FilmPersonUpdatedEvent;
import org.movierc.filmpersonservice.dto.FilmPersonDto;
import org.movierc.filmpersonservice.entity.FilmPerson;

@Mapper
public interface FilmPersonMapper {
    FilmPersonDto filmPersonToFilmPersonDto(FilmPerson filmPerson);

    FilmPersonCreatedEvent filmPersonToFilmPersonCreatedEvent(FilmPerson filmPerson);

    FilmPersonUpdatedEvent filmPersonToFilmPersonUpdatedEvent(FilmPerson filmPerson);

    FilmPersonDeletedEvent filmPersonToFilmPersonDeletedEvent(FilmPerson filmPerson);
}
