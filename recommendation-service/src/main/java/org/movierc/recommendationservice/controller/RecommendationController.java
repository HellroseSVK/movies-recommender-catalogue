package org.movierc.recommendationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.facade.RecommendationFacade;
import org.movierc.shared.security.OAuth2;
import org.movierc.shared.security.OAuth2Scopes;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/recommendation")
@SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.READ_SCOPE})
@RestController
@AllArgsConstructor
public class RecommendationController {
    private final RecommendationFacade recommendationFacade;

    @Operation(
            summary = "Get recommendations in selected categories above threshold, default is 0",
            description = "Get a list of recommendations for the user based on the selected categories. The recommendations " +
                    "are based on all user average ratings for movie, the threshold is the minimum average rating " +
                    "for the movie to be included in the recommendations. The default threshold is 0, which means all movies " +
                    "in the selected categories will be included in the recommendations. The recommendations are sorted by " +
                    "rating in descending order."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - access token not provided or not valid",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient scope",
                            content = @Content()
                    ),
                    @ApiResponse(responseCode = "404", description = "Category not found", content = @Content(mediaType = "application/problem+json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping("/by-categories/{threshold}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByCategoriesAboveThreshold(@RequestParam List<UUID> categoryIds, @PathVariable @DefaultValue(value = "0") int threshold) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByCategoriesAboveThreshold(new HashSet<>(categoryIds), threshold));
    }

    @Operation(
            summary = "Get recommendations based on movie",
            description = "Get a list of recommendations based on what other users, who rated this movie, liked. " +
                    "The recommendations are sorted by rating in descending order."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Recommendations retrieved successfully"),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - access token not provided or not valid",
                            content = @Content()
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient scope",
                            content = @Content()
                    ),
                    @ApiResponse(responseCode = "404", description = "Movie not found", content = @Content(mediaType = "application/problem+json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
            }
    )
    @GetMapping("/by-movie/{movieId}")
    public ResponseEntity<List<MovieDto>> getRecommendationsByMovieId(@PathVariable(name = "movieId") UUID movieId) {
        return ResponseEntity.ok(recommendationFacade.getRecommendationsByMovieId(movieId));
    }
}
