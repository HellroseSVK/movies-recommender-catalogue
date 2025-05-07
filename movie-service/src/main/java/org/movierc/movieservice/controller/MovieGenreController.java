package org.movierc.movieservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.facade.MovieFacade;
import org.movierc.shared.security.OAuth2;
import org.movierc.shared.security.OAuth2Scopes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/api/v1/movie/{movieId}/genre")
@RestController
@AllArgsConstructor
@SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.WRITE_SCOPE_MOVIE})
public class MovieGenreController {
    private final MovieFacade movieFacade;

    @PutMapping("/{genreId}")
    @Operation(
            summary = "Assign a genre to a movie",
            description = "Assign a genre to a movie"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully assigned genre to movie"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - access token not provided or not valid"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient scope"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie or genre not found",
                            content = @Content(mediaType = "application/problem+json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    ),
            }
    )
    public ResponseEntity<MovieDto> assignGenre(@PathVariable UUID movieId, @PathVariable UUID genreId) {
        return ResponseEntity.ok(movieFacade.assignGenre(movieId, genreId));
    }

    @DeleteMapping("/{genreId}")
    @Operation(
            summary = "Remove a genre from a movie",
            description = "Remove a genre from a movie"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully removed genre from movie"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - access token not provided or not valid"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient scope"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie or genre not found",
                            content = @Content(mediaType = "application/problem+json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    ),
            }
    )
    public ResponseEntity<MovieDto> removeGenre(@PathVariable UUID movieId, @PathVariable UUID genreId) {
        return ResponseEntity.ok(movieFacade.removeGenre(movieId, genreId));
    }
}
