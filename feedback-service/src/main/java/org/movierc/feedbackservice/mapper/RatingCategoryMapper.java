package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.rating.RatingCategoryCreatedEvent;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.entity.RatingCategory;

@Mapper
public interface RatingCategoryMapper {
    RatingCategoryDto ratingCategoryToRatingCategoryDto(RatingCategory ratingCategory);

    RatingCategory ratingCategoryDtoToRatingCategory(RatingCategoryDto ratingCategoryDto);

    RatingCategoryCreatedEvent ratingCategoryToRatingCategoryCreatedEvent(RatingCategory ratingCategory);
}
