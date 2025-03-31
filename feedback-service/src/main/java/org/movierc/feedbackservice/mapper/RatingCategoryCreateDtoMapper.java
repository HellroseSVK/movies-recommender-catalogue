package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.feedbackservice.dto.RatingCategoryCreateDto;
import org.movierc.feedbackservice.entity.RatingCategory;

import java.util.UUID;

@Mapper(imports = {UUID.class})
public interface RatingCategoryCreateDtoMapper {
    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    RatingCategory ratingCategoryCreateDtoToRatingCategory(RatingCategoryCreateDto ratingCategory);
}
