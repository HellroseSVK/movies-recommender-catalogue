package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.events.rating.MovieRatingDeletedEvent;
import org.movierc.events.rating.MovieRatingUpdatedEvent;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.entity.UserMovieRating;

@Mapper (uses = {UserMapper.class, MovieMapper.class})
public interface MovieRatingMapper {

    UserMovieRatingDto userMovieRatingTouserMovieRatingDto(UserMovieRating userMovieRating);

    UserMovieRating userMovieRatingDtoToUserMovieRating(UserMovieRatingDto userMovieRatingDto);

    MovieRatingCreatedEvent userMovieRatingToMovieRatingCreatedEvent(UserMovieRating userMovieRating);

    MovieRatingUpdatedEvent userMovieRatingToMovieRatingUpdatedEvent(UserMovieRating userMovieRating);

    MovieRatingDeletedEvent userMovieRatingToMovieRatingDeletedEvent(UserMovieRating userMovieRating);
}
