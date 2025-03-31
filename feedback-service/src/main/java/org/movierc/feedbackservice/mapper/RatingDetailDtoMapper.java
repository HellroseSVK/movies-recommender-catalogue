package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.feedbackservice.dto.RatingDetailDto;
import org.movierc.feedbackservice.entity.RatingDetail;

@Mapper
public interface RatingDetailDtoMapper {
    RatingDetail ratingDetailDtoToRatingDetail(RatingDetailDto ratingDetailDto);
}
