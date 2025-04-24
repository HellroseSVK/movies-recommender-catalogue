package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.UserMovieRating;

import java.util.UUID;

@Mapper(
        imports = {UUID.class}
)
public interface UserMovieRatingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "ratingDetails", ignore = true)
    UserMovieRating userMovieRatingCreatedEventToUserMovieRating(
            MovieRatingCreatedEvent userMovieRatingCreatedEvent,
            Movie movie
    );
}
