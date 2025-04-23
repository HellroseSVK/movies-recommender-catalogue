package org.movierc.recommendationservice.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.movierc.recommendationservice.entity.AverageMovieRating;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.exception.CategoryNotFoundException;
import org.movierc.recommendationservice.exception.MovieNotFoundException;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendationService {
    private final MovieRepository movieRepository;
    private final RatingCategoryRepository ratingCategoryRepository;

    @Transactional
    public Map<Movie, List<AverageMovieRating>> getRecommendationsByCategoriesAboveThreshold(
            Set<UUID> categoryIds,
            int threshold
    ) {
        var categories = ratingCategoryRepository.findByIdIn(categoryIds);
        categoryIds.forEach(categoryId -> {
            if (categories.stream().noneMatch(category -> category.getId().equals(categoryId))) {
                throw new CategoryNotFoundException(categoryId);
            }
        });

        var similarMovies = movieRepository.findRecommendationsByCategoriesAboveThreshold(categoryIds, threshold, Pageable.unpaged());

        return getAverageRatingsForMovies(similarMovies);
    }

    @Transactional
    public Map<Movie, List<AverageMovieRating>> getRecommendationsByMovieId(UUID movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException(movieId);
        }

        var similarMovies = movieRepository.findSimilarMovies(movieId, Pageable.unpaged());

        return getAverageRatingsForMovies(similarMovies);
    }

    private Map<Movie, List<AverageMovieRating>> getAverageRatingsForMovies(List<Movie> similarMovies) {
        var averageRatings = movieRepository.findAverageMovieRatingsByMovieIdIn(
                similarMovies.stream().map(Movie::getId).collect(Collectors.toSet()), Pageable.unpaged()
        );

        return similarMovies.stream()
                .collect(Collectors.toMap(
                        movie -> movie,
                        movie -> averageRatings.stream()
                                .filter(averageMovieRating -> averageMovieRating.movieId().equals(movie.getId()))
                                .collect(Collectors.toList())
                ));
    }

}