package org.movierc.movieservice.mappers;

import org.mapstruct.Mapper;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.entity.Movie;

@Mapper(uses = {
        MovieMapper.MovieCreatedEventGenreMapper.class,
        MovieMapper.MovieCreatedEventFilmPersonMapper.class
})
public interface MovieMapper {
    MovieDto movieToMovieDto(Movie movie);

    MovieCreatedEvent movieToMovieCreatedEvent(Movie movie);

    @Mapper
    interface MovieCreatedEventGenreMapper {
        MovieCreatedEvent.Genre movieGenreToMovieCreatedEventGenre(Genre genre);
    }

    @Mapper
    interface MovieCreatedEventFilmPersonMapper {
        MovieCreatedEvent.FilmPerson filmPersonToMovieCreatedEventFilmPerson(FilmPerson genre);
    }
}
