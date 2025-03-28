package org.movierc.movieservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.genre.GenreCreatedEvent;
import org.movierc.events.genre.GenreDeletedEvent;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.entity.Genre;

@Mapper
public interface GenreMapper {
    GenreDto genreToGenreDto(Genre genre);

    GenreCreatedEvent genreToGenreCreatedEvent(Genre genre);
    GenreDeletedEvent genreToGenreDeletedEvent(Genre genre);
}
