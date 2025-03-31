package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.entity.UserMovieRating;

import java.util.UUID;

@Mapper(
        imports = {UUID.class}
)
public interface UserMovieRatingCreateDtoMapper {
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    UserMovieRating userMovieRatingCreateDtoToUserMovieRating(UserMovieRatingCreateDto userMovieRating);
}
