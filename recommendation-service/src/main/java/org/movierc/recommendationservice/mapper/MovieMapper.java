package org.movierc.recommendationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.dto.RatingDetailDto;
import org.movierc.recommendationservice.entity.AverageMovieRating;
import org.movierc.recommendationservice.entity.Movie;

import java.util.List;

@Mapper
public interface MovieMapper {
    @Mapping(target = "userAverageRatings", source = "userRatings")
    MovieDto movieToMovieDto(Movie movie, List<AverageMovieRating> userRatings);

    @Mapping(target = "category.id", source = "ratingCategoryId")
    @Mapping(target = "category.name", source = "ratingCategoryName")
    @Mapping(target = "rating", source = "averageRating")
    RatingDetailDto averageMovieRatingToRatingDetailDto(AverageMovieRating averageMovieRating);
}
