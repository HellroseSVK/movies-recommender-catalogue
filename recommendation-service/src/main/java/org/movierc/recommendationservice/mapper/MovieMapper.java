package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.dto.RatingDetailDto;
import org.movierc.recommendationservice.entity.Movie;

import java.util.List;

@Mapper
public interface MovieMapper {
    MovieDto movieToMovieDto(Movie movie, List<RatingDetailDto> userRatings);
}
