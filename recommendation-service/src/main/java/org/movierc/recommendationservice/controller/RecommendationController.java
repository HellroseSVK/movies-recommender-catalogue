package org.movierc.recommendationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(
            summary = "Get all recommendations",
            description = "Get a list of recommended movies for the user. The recommendations are based on all user average " +
                    "ratings for movies in the database. The recommendations are sorted by rating in descending order."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<List<MovieDto>> getRecommendations() {
        return ResponseEntity.ok(recommendationFacade.getRecommendations());
    }

    @Operation(
            summary = "Get recommendations in selected categories above threshold, default is 0",
            description = "Get a list of recommendations for the user based on the selected categories. The recommendations " +
                    "are based on all user average ratings in selected for movie, the threshold is the minimum average rating " +
                    "for the movie to be included in the recommendations. The default threshold is 0, which means all movies " +
                    "in the selected categories will be included in the recommendations. The recommendations are sorted by " +
                    "rating in descending order."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/by-categories/{threshold}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByCategoriesAboveThreshold(@RequestParam List<UUID> categoryIds, @PathVariable @DefaultValue(value = "0") int threshold) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByCategoriesAboveThreshold(categoryIds, threshold));
    }

    @Operation(
            summary = "Get recommendations in selected categories weighted by the user",
            description = "Get a list of recommendations for the user based on the selected categories. The recommendations " +
                    "are based on all users weighted average based on category. The weights are used as multiplication " +
                    "factors for the average rating of each category. The recommendations are sorted by rating in descending order."

    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/by-categories/weighted")
    public ResponseEntity<List<MovieDto>> getRecommendationsByCategoriesWeighted(@RequestParam List<GetRecommendationByCategoriesWeightedDto> categoryWeights) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByCategoriesWeighted(categoryWeights));
    }

    @Operation(
            summary = "Get recommendations in already rated movies",
            description = "Get a list of recommendations for the user based on the past ratings. The recommendations " +
                    "are based on user average ratings in selected for movie, The recommendations are sorted by rating " +
                    "in descending order."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/by-rated/{userId}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByRated(@PathVariable(name = "userId") UUID userId) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByRated(userId));
    }

    @Operation(
            summary = "Get recommendations based on categories already rated, above a threshold, default is 0",
            description = "Get a list of recommendations for the user based on categories already rated. The recommendations " +
                    "are based on all user average ratings in selected for movie, the threshold is the minimum average rating " +
                    "for the movie to be included in the recommendations. The default threshold is 0, which means all movies " +
                    "in the categories already rated will be included in the recommendations. The recommendations are sorted by " +
                    "rating in descending order."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/by-rated/{userId}/all-similar/{threshold}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByRatedAllSimilarAboveThreshold(@PathVariable(name = "userId") UUID userId, @PathVariable @DefaultValue(value = "70") int threshold) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByRatedAllSimilarAboveThreshold(userId, threshold));
    }

    @Operation(
            summary = "Get recommendations based on movie",
            description = "Get a list of recommendations for the user based on what other users, who also rated the same movie, rated. " +
                    "The recommendations are based on users who also rated the same movie average ratings in selected movie, The " +
                    "recommendations are sorted by rating in descending order."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/by-movie/{movieId}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByMovieId(@PathVariable(name = "movieId") UUID movieId) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByMovieId(movieId));
    }
}
