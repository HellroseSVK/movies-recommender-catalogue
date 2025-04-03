package org.movierc.recommendationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.recommendationservice.dto.GetRecommendationByCategoriesWeightedDto;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.dto.RatingCategoryDto;
import org.movierc.recommendationservice.dto.RatingDetailDto;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.RatingDetail;
import org.movierc.recommendationservice.entity.UserMovieRating;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.movierc.recommendationservice.mapper.MovieMapper;
import org.movierc.recommendationservice.mapper.RatingCategoryMapper;
import org.movierc.recommendationservice.mapper.RatingDetailMapper;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.movierc.recommendationservice.repository.UserMovieRatingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecommendationServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private RatingCategoryRepository ratingCategoryRepository;

    @Mock
    private UserMovieRatingRepository userMovieRatingRepository;

    @Mock
    private MovieMapper movieMapper;

    @Mock
    private RatingDetailMapper ratingDetailMapper;

    @Mock
    private RatingCategoryMapper ratingCategoryMapper;

    private RecommendationService recommendationService;


    @BeforeEach
    void setUp() {
        recommendationService = new RecommendationService(movieRepository, ratingCategoryRepository, userMovieRatingRepository, movieMapper, ratingDetailMapper, ratingCategoryMapper);
    }

    @Test
    void getRecommendations_ReturnsListOfMovieDtos() {
        // Setup
        Movie movie = new Movie(UUID.randomUUID(), "Test Movie");
        UserMovieRating userMovieRating = new UserMovieRating(UUID.randomUUID(), movie.getId(), new ArrayList<>());
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(userMovieRatingRepository.findByMovieId(movie.getId())).thenReturn(List.of(userMovieRating));
        when(movieMapper.movieToMovieDto(any(), any())).thenReturn(new MovieDto());

        // Act
        List<MovieDto> result = recommendationService.getRecommendations();

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRecommendationsByCategoriesAboveThreshold_ReturnsFilteredMovieDtos() {
        // Setup
        UUID categoryId = UUID.randomUUID();
        Movie movie = new Movie(UUID.randomUUID(), "Test Movie");
        RatingDetail ratingDetail = new RatingDetail(categoryId, 80);
        UserMovieRating userMovieRating = new UserMovieRating(UUID.randomUUID(), movie.getId(), List.of(ratingDetail));
        RatingCategory ratingCategory = new RatingCategory(categoryId, "Test Category");
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(userMovieRatingRepository.findByMovieId(movie.getId())).thenReturn(List.of(userMovieRating));
        when(ratingCategoryRepository.findById(categoryId)).thenReturn(Optional.of(ratingCategory));
        when(ratingCategoryMapper.ratingCategoryToRatingCategoryDto(any())).thenReturn(new RatingCategoryDto());
        when(ratingDetailMapper.ratingDetailToRatingDetailDto(any(), any())).thenReturn(new RatingDetailDto());
        when(movieMapper.movieToMovieDto(any(), any())).thenReturn(new MovieDto());


        // Act
        List<MovieDto> result = recommendationService.getRecommendationsByCategoriesAboveThreshold(List.of(categoryId), 70);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRecommendationsByCategoriesWeighted_ReturnsWeightedMovieDtos() {
        // Setup
        UUID categoryId = UUID.randomUUID();
        GetRecommendationByCategoriesWeightedDto weightedDto = new GetRecommendationByCategoriesWeightedDto(categoryId, 0.5);

        Movie movie = new Movie(UUID.randomUUID(), "Test Movie");
        RatingDetail ratingDetail = new RatingDetail(categoryId, 80);
        UserMovieRating userMovieRating = new UserMovieRating(UUID.randomUUID(), movie.getId(), List.of(ratingDetail));
        RatingCategory ratingCategory = new RatingCategory(categoryId, "Test Category");

        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(userMovieRatingRepository.findByMovieId(movie.getId())).thenReturn(List.of(userMovieRating));
        when(ratingCategoryRepository.findById(categoryId)).thenReturn(Optional.of(ratingCategory));
        when(ratingCategoryMapper.ratingCategoryToRatingCategoryDto(any())).thenReturn(new RatingCategoryDto());
        when(ratingDetailMapper.ratingDetailToRatingDetailDto(any(), any())).thenReturn(new RatingDetailDto());
        when(movieMapper.movieToMovieDto(any(), any())).thenReturn(new MovieDto());

        // Act
        List<MovieDto> result = recommendationService.getRecommendationsByCategoriesWeighted(List.of(weightedDto));

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRecommendationsByRated_ReturnsMovieDtosForUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        Movie movie = new Movie(UUID.randomUUID(), "Test Movie");
        UserMovieRating userMovieRating = new UserMovieRating(userId, movie.getId(), new ArrayList<>());
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        when(userMovieRatingRepository.findByMovieId(movie.getId())).thenReturn(List.of(userMovieRating));
        when(movieMapper.movieToMovieDto(any(), any())).thenReturn(new MovieDto());

        // Act
        List<MovieDto> result = recommendationService.getRecommendationsByRated(userId);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getRecommendationsByRatedAllSimilarAboveThreshold_ReturnsFilteredMovieDtos() {
        // Setup
        UUID userId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        RatingDetail ratingDetail = new RatingDetail(categoryId, 80);
        UserMovieRating userMovieRating = new UserMovieRating(userId, UUID.randomUUID(), List.of(ratingDetail));
        when(userMovieRatingRepository.findByUserId(userId)).thenReturn(List.of(userMovieRating));
        when(movieRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        List<MovieDto> result = recommendationService.getRecommendationsByRatedAllSimilarAboveThreshold(userId, 70);

        // Verify
        assertNotNull(result);
    }

    @Test
    void getRecommendationsByMovieId_ReturnsMovieDtosForMovie() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID otherMovieId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        RatingDetail ratingDetail = new RatingDetail(UUID.randomUUID(), 90);
        UserMovieRating userMovieRatingOriginal = new UserMovieRating(userId, movieId, List.of());
        UserMovieRating userMovieRatingOther = new UserMovieRating(userId, otherMovieId, List.of(ratingDetail));
        Movie movie1 = new Movie(movieId, "Original Movie");
        Movie movie2 = new Movie(otherMovieId, "Recommended Movie");

        RatingCategory ratingCategory = new RatingCategory(ratingDetail.getRatingCategoryId(), "Test Category");

        when(userMovieRatingRepository.findByMovieId(movieId)).thenReturn(List.of(userMovieRatingOriginal));
        when(userMovieRatingRepository.findByMovieId(otherMovieId)).thenReturn(List.of(userMovieRatingOther));
        when(movieRepository.findAll()).thenReturn(List.of(movie1, movie2));

        when(ratingCategoryRepository.findById(ratingDetail.getRatingCategoryId()))
            .thenReturn(Optional.of(ratingCategory));
        when(ratingCategoryMapper.ratingCategoryToRatingCategoryDto(any()))
            .thenReturn(new RatingCategoryDto());
        when(ratingDetailMapper.ratingDetailToRatingDetailDto(any(), any()))
            .thenReturn(new RatingDetailDto());
        when(movieMapper.movieToMovieDto(any(), any())).thenReturn(new MovieDto());

        // Act
        List<MovieDto> result = recommendationService.getRecommendationsByMovieId(movieId);

        // Verify
        assertNotNull(result);
        assertEquals(1, result.size());
    }

}