package org.movierc.movieservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieDeletedEvent;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.entity.Movie;

@Mapper
public interface MovieMapper {
    MovieDto movieToMovieDto(Movie movie);

    MovieCreatedEvent movieToMovieCreatedEvent(Movie movie);

    MovieUpdatedEvent movieToMovieUpdatedEvent(Movie movie);

    MovieDeletedEvent movieToMovieDeletedEvent(Movie movie);
}
