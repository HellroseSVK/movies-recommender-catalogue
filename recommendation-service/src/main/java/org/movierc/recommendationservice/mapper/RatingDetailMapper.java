package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.recommendationservice.dto.RatingCategoryDto;
import org.movierc.recommendationservice.dto.RatingDetailDto;
import org.movierc.recommendationservice.entity.RatingDetail;

@Mapper
public interface RatingDetailMapper {
    @Mapping(target = "category", source = "ratingCategoryDto")
    @Mapping(target = "rating", source = "ratingDetail.rating")
    RatingDetailDto ratingDetailToRatingDetailDto(RatingDetail ratingDetail, RatingCategoryDto ratingCategoryDto);
}
