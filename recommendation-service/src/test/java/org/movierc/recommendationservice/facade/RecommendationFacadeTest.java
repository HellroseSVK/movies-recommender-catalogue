package org.movierc.recommendationservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.AverageMovieRating;
import org.movierc.recommendationservice.mapper.MovieMapper;
import org.movierc.recommendationservice.service.RecommendationService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecommendationFacadeTest {

    @Mock
    private RecommendationService recommendationService;
    @Mock
    private MovieMapper movieMapper;

    @InjectMocks
    private RecommendationFacade recommendationFacade;


    @Test
    void getRecommendationsByCategoriesAboveThreshold_HasMovieAboveThreshold_ReturnsMovieDtoList() {
        UUID categoryId = UUID.randomUUID();
        int threshold = 5;
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie(movieId, "Test Movie", Collections.emptyList());
        AverageMovieRating avgRating = new AverageMovieRating(movieId, UUID.randomUUID(), "Category", 8.0);
        Map<Movie, List<AverageMovieRating>> serviceResult = Map.of(movie, List.of(avgRating));
        MovieDto movieDto = MovieDto.builder()
            .id(movieId)
            .title("Test Movie")
            .userAverageRatings(Collections.emptyList())
            .build();
        when(recommendationService.getRecommendationsByCategoriesAboveThreshold(Set.of(categoryId), threshold))
            .thenReturn(serviceResult);
        when(movieMapper.movieToMovieDto(movie, List.of(avgRating))).thenReturn(movieDto);

        List<MovieDto> result = recommendationFacade.getRecommendationsByCategoriesAboveThreshold(Set.of(categoryId), threshold);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(movieDto, result.get(0));
        verify(recommendationService).getRecommendationsByCategoriesAboveThreshold(Set.of(categoryId), threshold);
        verify(movieMapper).movieToMovieDto(movie, List.of(avgRating));
    }

    @Test
    void getRecommendationsByMovieId_HasRatedWithSamePerson_ReturnsMovieDtoList() {
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie(movieId, "Test Movie", Collections.emptyList());
        AverageMovieRating avgRating = new AverageMovieRating(movieId, UUID.randomUUID(), "Category", 9.0);
        Map<Movie, List<AverageMovieRating>> serviceResult = Map.of(movie, List.of(avgRating));
        MovieDto movieDto = MovieDto.builder()
            .id(movieId)
            .title("Test Movie")
            .userAverageRatings(Collections.emptyList())
            .build();
        when(recommendationService.getRecommendationsByMovieId(movieId)).thenReturn(serviceResult);
        when(movieMapper.movieToMovieDto(movie, List.of(avgRating))).thenReturn(movieDto);

        List<MovieDto> result = recommendationFacade.getRecommendationsByMovieId(movieId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(movieDto, result.get(0));
        verify(recommendationService).getRecommendationsByMovieId(movieId);
        verify(movieMapper).movieToMovieDto(movie, List.of(avgRating));
    }
}