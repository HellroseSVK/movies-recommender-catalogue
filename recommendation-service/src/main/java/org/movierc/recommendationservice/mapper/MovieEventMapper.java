package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.recommendationservice.entity.Movie;

@Mapper
public interface MovieEventMapper {
    Movie movieCreatedEventToMovie(MovieCreatedEvent movieCreatedEvent);

    Movie movieUpdatedEventToMovie(MovieUpdatedEvent movieUpdatedEvent);
}
