package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.events.rating.RatingCategoryCreatedEvent;
import org.movierc.recommendationservice.dto.RatingCategoryDto;
import org.movierc.recommendationservice.entity.RatingCategory;

@Mapper
public interface RatingCategoryMapper {
    RatingCategory ratingCategoryCreatedEventToRatingCategory(RatingCategoryCreatedEvent ratingCategoryCreatedEvent);

    RatingCategoryDto ratingCategoryToRatingCategoryDto(RatingCategory ratingCategory);
}
