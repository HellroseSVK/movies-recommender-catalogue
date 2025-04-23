package org.movierc.recommendationservice.entity;

import java.util.UUID;

public record AverageMovieRating(
        UUID movieId,
        UUID ratingCategoryId,
        String ratingCategoryName,
        double averageRating
) {
}
