package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.events.rating.MovieRatingDeletedEvent;
import org.movierc.events.rating.MovieRatingUpdatedEvent;
import org.movierc.recommendationservice.entity.UserMovieRating;

@Mapper
public interface UserMovieRatingMapper {
    UserMovieRating userMovieRatingCreatedEventToUserMovieRating(MovieRatingCreatedEvent userMovieRatingCreatedEvent);

    @Mapping(target = "ratingDetails", source = "newRatingDetails")
    UserMovieRating userMovieRatingUpdatedEventToUserMovieRating(MovieRatingUpdatedEvent userMovieRatingUpdatedEvent);

    UserMovieRating userMovieRatingDeletedEventToUserMovieRating(MovieRatingDeletedEvent userMovieRatingDeletedEvent);
}
