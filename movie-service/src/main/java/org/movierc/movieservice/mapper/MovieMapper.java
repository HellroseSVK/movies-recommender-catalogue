package org.movierc.movieservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieDeletedEvent;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.entity.Movie;

@Mapper(uses = {
        MovieMapper.MovieCreatedEventGenreMapper.class,
        MovieMapper.MovieCreatedEventFilmPersonMapper.class,
        MovieMapper.MovieUpdatedEventGenreMapper.class,
        MovieMapper.MovieUpdatedEventFilmPersonMapper.class
})
public interface MovieMapper {
    MovieDto movieToMovieDto(Movie movie);

    MovieCreatedEvent movieToMovieCreatedEvent(Movie movie);

    MovieUpdatedEvent movieToMovieUpdatedEvent(Movie movie);

    MovieDeletedEvent movieToMovieDeletedEvent(Movie movie);

    @Mapper
    interface MovieCreatedEventGenreMapper {
        MovieCreatedEvent.Genre movieGenreToMovieCreatedEventGenre(Genre genre);
    }

    @Mapper
    interface MovieCreatedEventFilmPersonMapper {
        MovieCreatedEvent.FilmPerson filmPersonToMovieCreatedEventFilmPerson(FilmPerson genre);
    }

    @Mapper
    interface MovieUpdatedEventGenreMapper {
        MovieUpdatedEvent.Genre movieGenreToMovieUpdatedEventGenre(Genre genre);
    }

    @Mapper
    interface MovieUpdatedEventFilmPersonMapper {
        MovieUpdatedEvent.FilmPerson filmPersonToMovieUpdatedEventFilmPerson(FilmPerson genre);
    }
}
