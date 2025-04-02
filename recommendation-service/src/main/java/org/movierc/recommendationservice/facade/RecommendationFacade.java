package org.movierc.recommendationservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.recommendationservice.dto.GetRecommendationByCategoriesWeightedDto;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.service.RecommendationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RecommendationFacade {
    private final RecommendationService recommendationService;

    public List<MovieDto> getRecommendations() {
        return recommendationService.getRecommendations();
    }

    public List<MovieDto> getRecommendationsByCategoriesAboveThreshold(List<UUID> categoryIds, int threshold){
        return recommendationService.getRecommendationsByCategoriesAboveThreshold(categoryIds, threshold);
    }


    public List<MovieDto> getRecommendationsByCategoriesWeighted(List<GetRecommendationByCategoriesWeightedDto> categoryWeights) {
        return recommendationService.getRecommendationsByCategoriesWeighted(categoryWeights);
    }

    public List<MovieDto> getRecommendationsByRated(UUID userId) {
        return recommendationService.getRecommendationsByRated(userId);
    }

    public List<MovieDto> getRecommendationsByRatedAllSimilarAboveThreshold(UUID userId, int threshold){
        return recommendationService.getRecommendationsByRatedAllSimilarAboveThreshold(userId, threshold);
    }

    public List<MovieDto> getRecommendationsByMovieId(UUID movieId) {
        return recommendationService.getRecommendationsByMovieId(movieId);
    }
}
