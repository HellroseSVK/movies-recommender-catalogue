package org.movierc.recommendationservice.controller;

import lombok.AllArgsConstructor;
import org.movierc.recommendationservice.dto.GetRecommendationByCategoriesWeightedDto;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.facade.RecommendationFacade;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/recommendation")
@RestController
@AllArgsConstructor
public class RecommendationController {
    private final RecommendationFacade recommendationFacade;

    @GetMapping
    public ResponseEntity<List<MovieDto>> getRecommendations() {
        return ResponseEntity.ok(recommendationFacade.getRecommendations());
    }

    @GetMapping("/by-categories/{threshold}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByCategoriesAboveThreshold(@RequestParam List<UUID> categoryIds, @PathVariable(name = "threshold") @DefaultValue(value = "0") int threshold) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByCategoriesAboveThreshold(categoryIds, threshold));
    }

    @GetMapping("/by-categories/weighted")
    public ResponseEntity<List<MovieDto>> getRecommendationsByCategoriesWeighted(@RequestParam List<GetRecommendationByCategoriesWeightedDto> categoryWeights) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByCategoriesWeighted(categoryWeights));
    }

    @GetMapping("/by-rated/{userId}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByRated(@PathVariable(name = "userId") UUID userId) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByRated(userId));
    }

    @GetMapping("/by-rated/{userId}/all-similar/{threshold}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByRatedAllSimilarAboveThreshold(@PathVariable(name = "userId") UUID userId, @PathVariable(name = "threshold") @DefaultValue(value = "70") int threshold) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByRatedAllSimilarAboveThreshold(userId, threshold));
    }
}
