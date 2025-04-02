package org.movierc.recommendationservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.recommendationservice.dto.GetRecommendationByCategoriesWeightedDto;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.service.RecommendationService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationFacadeTest {

    @Mock
    private RecommendationService recommendationService;

    @InjectMocks
    private RecommendationFacade recommendationFacade;

    @Test
    void getRecommendations_ReturnsMovieList() {
        // Setup
        MovieDto movieDto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("Test Movie")
            .build();
        when(recommendationService.getRecommendations()).thenReturn(List.of(movieDto));

        // Act
        List<MovieDto> result = recommendationFacade.getRecommendations();

        // Verify
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getTitle());
        verify(recommendationService).getRecommendations();
    }

    @Test
    void getRecommendationsByCategoriesAboveThreshold_ReturnsMovieList() {
        // Setup
        UUID categoryId = UUID.randomUUID();
        MovieDto movieDto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("Test Movie")
            .build();
        when(recommendationService.getRecommendationsByCategoriesAboveThreshold(List.of(categoryId), 5))
            .thenReturn(List.of(movieDto));

        // Act
        List<MovieDto> result = recommendationFacade.getRecommendationsByCategoriesAboveThreshold(List.of(categoryId), 5);

        // Verify
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getTitle());
        verify(recommendationService).getRecommendationsByCategoriesAboveThreshold(List.of(categoryId), 5);
    }

    @Test
    void getRecommendationsByCategoriesWeighted_ReturnsMovieList() {
        // Setup
        GetRecommendationByCategoriesWeightedDto weightedDto = GetRecommendationByCategoriesWeightedDto.builder()
            .categoryId(UUID.randomUUID())
            .weight(0.5)
            .build();
        MovieDto movieDto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("Test Movie")
            .build();
        when(recommendationService.getRecommendationsByCategoriesWeighted(List.of(weightedDto)))
            .thenReturn(List.of(movieDto));

        // Act
        List<MovieDto> result = recommendationFacade.getRecommendationsByCategoriesWeighted(List.of(weightedDto));

        // Verify
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getTitle());
        verify(recommendationService).getRecommendationsByCategoriesWeighted(List.of(weightedDto));
    }

    @Test
    void getRecommendationsByRated_ReturnsMovieList() {
        // Setup
        UUID userId = UUID.randomUUID();
        MovieDto movieDto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("Test Movie")
            .build();
        when(recommendationService.getRecommendationsByRated(userId)).thenReturn(List.of(movieDto));

        // Act
        List<MovieDto> result = recommendationFacade.getRecommendationsByRated(userId);

        // Verify
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getTitle());
        verify(recommendationService).getRecommendationsByRated(userId);
    }

    @Test
    void getRecommendationsByRatedAllSimilarAboveThreshold_ReturnsMovieList() {
        // Setup
        UUID userId = UUID.randomUUID();
        MovieDto movieDto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("Test Movie")
            .build();
        when(recommendationService.getRecommendationsByRatedAllSimilarAboveThreshold(userId, 70))
            .thenReturn(List.of(movieDto));

        // Act
        List<MovieDto> result = recommendationFacade.getRecommendationsByRatedAllSimilarAboveThreshold(userId, 70);

        // Verify
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getTitle());
        verify(recommendationService).getRecommendationsByRatedAllSimilarAboveThreshold(userId, 70);
    }

    @Test
    void getRecommendationsByMovieId_ReturnsMovieList() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieDto movieDto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("Test Movie")
            .build();
        when(recommendationService.getRecommendationsByMovieId(movieId)).thenReturn(List.of(movieDto));

        // Act
        List<MovieDto> result = recommendationFacade.getRecommendationsByMovieId(movieId);

        // Verify
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getTitle());
        verify(recommendationService).getRecommendationsByMovieId(movieId);
    }
}