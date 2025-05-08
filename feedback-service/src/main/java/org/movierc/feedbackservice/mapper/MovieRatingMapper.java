package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.events.rating.MovieRatingChangedEvent;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.events.rating.MovieRatingDeletedEvent;
import org.movierc.events.rating.MovieRatingUpdatedEvent;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.entity.UserMovieRating;

@Mapper(uses = {MovieMapper.class})
public interface MovieRatingMapper {

    UserMovieRatingDto userMovieRatingTouserMovieRatingDto(UserMovieRating userMovieRating);

    UserMovieRating userMovieRatingDtoToUserMovieRating(UserMovieRatingDto userMovieRatingDto);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "movieId", source = "movie.id")
    MovieRatingCreatedEvent userMovieRatingToMovieRatingCreatedEvent(UserMovieRating userMovieRating);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "movieId", source = "movie.id")
    MovieRatingUpdatedEvent userMovieRatingToMovieRatingUpdatedEvent(UserMovieRating userMovieRating);

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "movieId", source = "movie.id")
    MovieRatingDeletedEvent userMovieRatingToMovieRatingDeletedEvent(UserMovieRating userMovieRating);

    @Mapping(target = "newRatingDetails", source = "ratingDetails")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "movieId", source = "movie.id")
    MovieRatingChangedEvent userMovieRatingToMovieRatingChangedEvent(UserMovieRating userMovieRating);
}
