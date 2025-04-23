package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.events.rating.RatingCategoryCreatedEvent;
import org.movierc.recommendationservice.dto.RatingCategoryDto;
import org.movierc.recommendationservice.entity.RatingCategory;

@Mapper
public interface RatingCategoryMapper {
    @Mapping(target = "ratingDetails", ignore = true)
    RatingCategory ratingCategoryCreatedEventToRatingCategory(RatingCategoryCreatedEvent ratingCategoryCreatedEvent);
}
