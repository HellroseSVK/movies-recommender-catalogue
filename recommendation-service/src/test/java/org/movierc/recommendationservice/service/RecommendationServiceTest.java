package org.movierc.recommendationservice.service;

import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.movierc.recommendationservice.entity.AverageMovieRating;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.movierc.recommendationservice.exception.CategoryNotFoundException;
import org.movierc.recommendationservice.exception.MovieNotFoundException;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.movierc.recommendationservice.service.RecommendationService;
import org.springframework.data.domain.Pageable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    private MovieRepository movieRepository;
    private RatingCategoryRepository ratingCategoryRepository;
    private RecommendationService recommendationService;


    @BeforeEach
    void setUp() {
        movieRepository = mock(MovieRepository.class);
        ratingCategoryRepository = mock(RatingCategoryRepository.class);
        recommendationService = new RecommendationService(movieRepository, ratingCategoryRepository);
    }

    @Test
    void getRecommendationsByCategoriesAboveThreshold_Success() {
        UUID categoryId = UUID.randomUUID();
        int threshold = 10;
        Set<UUID> categoryIds = Set.of(categoryId);
        // Mock categories
        RatingCategory category = new RatingCategory(categoryId, "Category", Collections.emptyList());
        when(ratingCategoryRepository.findByIdIn(categoryIds)).thenReturn(List.of(category));
        // Mock recommended movies
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie(movieId, "Movie Title", Collections.emptyList());
        when(movieRepository.findRecommendationsByCategoriesAboveThreshold(categoryIds, threshold, Pageable.unpaged()))
            .thenReturn(List.of(movie));
        // Mock average ratings
        AverageMovieRating avg = new AverageMovieRating(movieId, categoryId, "Category", 12.0);
        when(movieRepository.findAverageMovieRatingsByMovieIdIn(Set.of(movieId), Pageable.unpaged()))
            .thenReturn(List.of(avg));

        Map<Movie, List<AverageMovieRating>> result = recommendationService.getRecommendationsByCategoriesAboveThreshold(categoryIds, threshold);

        assertNotNull(result);
        assertTrue(result.containsKey(movie));
        assertEquals(List.of(avg), result.get(movie));
    }

    @Test
    void getRecommendationsByCategoriesAboveThreshold_CategoryNotFound() {
        UUID categoryId = UUID.randomUUID();
        Set<UUID> categoryIds = Set.of(categoryId);
        when(ratingCategoryRepository.findByIdIn(categoryIds)).thenReturn(Collections.emptyList());
        assertThrows(CategoryNotFoundException.class,
            () -> recommendationService.getRecommendationsByCategoriesAboveThreshold(categoryIds, 5));
    }

    @Test
    void getRecommendationsByMovieId_Success() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.existsById(movieId)).thenReturn(true);
        Movie movie = new Movie(movieId, "Movie Title", Collections.emptyList());
        when(movieRepository.findSimilarMovies(movieId, Pageable.unpaged()))
            .thenReturn(List.of(movie));
        AverageMovieRating avg = new AverageMovieRating(movieId, UUID.randomUUID(), "Category", 9.0);
        when(movieRepository.findAverageMovieRatingsByMovieIdIn(Set.of(movieId), Pageable.unpaged()))
            .thenReturn(List.of(avg));

        Map<Movie, List<AverageMovieRating>> result = recommendationService.getRecommendationsByMovieId(movieId);

        assertNotNull(result);
        assertTrue(result.containsKey(movie));
        assertEquals(List.of(avg), result.get(movie));
    }

    @Test
    void getRecommendationsByMovieId_MovieNotFound() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.existsById(movieId)).thenReturn(false);
        assertThrows(MovieNotFoundException.class,
            () -> recommendationService.getRecommendationsByMovieId(movieId));
    }

}