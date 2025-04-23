package org.movierc.feedbackservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.feedbackservice.dto.RatingDetailCreateDto;
import org.movierc.feedbackservice.dto.RatingDetailDto;
import org.movierc.feedbackservice.entity.RatingDetail;

@Mapper
public interface RatingDetailMapper {
    RatingDetail ratingDetailDtoToRatingDetail(RatingDetailDto ratingDetailDto);

    RatingDetail ratingDetailCreateDtoToRatingDetail(RatingDetailCreateDto ratingDetail);
}
