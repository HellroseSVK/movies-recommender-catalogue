package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.recommendationservice.entity.Movie;

import java.util.ArrayList;

@Mapper(
        imports = {ArrayList.class}
)
public interface MovieEventMapper {
    @Mapping(target = "userMovieRatings", expression = "java(new ArrayList<>())")
    Movie movieCreatedEventToMovie(MovieCreatedEvent movieCreatedEvent);
}
