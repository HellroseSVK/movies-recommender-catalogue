package org.movierc.movieservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.facade.MovieFacade;
import org.movierc.shared.security.OAuth2;
import org.movierc.shared.security.OAuth2Scopes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/movie")
@RestController
@AllArgsConstructor
public class MovieController {

    private final MovieFacade movieFacade;

    @GetMapping
    @Operation(
            summary = "Get all movies",
            description = "Retrieve a list of all movies"
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.READ_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of movies"
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
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "text/plain")
                    ),
            }
    )
    public ResponseEntity<List<MovieDto>> getAllMovies() {
        return ResponseEntity.ok(movieFacade.getAllMovies());
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get movie by ID",
            description = "Retrieve a movie by its ID"
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.READ_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved movie"
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
                            description = "Movie not found",
                            content = @Content(mediaType = "application/problem+json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    ),
            }
    )
    public ResponseEntity<MovieDto> getMovieById(@PathVariable UUID id) {
        return ResponseEntity.ok(movieFacade.getMovieById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create a new movie",
            description = "Create a new movie with the provided details"
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.WRITE_SCOPE_MOVIE})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created movie"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(mediaType = "application/problem+json")
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
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    ),
            }
    )
    public ResponseEntity<MovieDto> createMovie(
            @RequestBody @Valid MovieCreateDto movieCreateDto
    ) {
        MovieDto createdMovie = movieFacade.createMovie(movieCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMovie);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a movie",
            description = "Update an existing movie with the provided details"
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.WRITE_SCOPE_MOVIE})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated movie"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(mediaType = "application/problem+json")
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
                            description = "Movie not found",
                            content = @Content(mediaType = "application/problem+json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    ),
            }
    )
    public ResponseEntity<MovieDto> updateMovie(
            @PathVariable UUID id,
            @RequestBody @Valid MovieUpdateDto movieUpdateDto
    ) {
        return ResponseEntity.ok(movieFacade.updateMovie(id, movieUpdateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a movie",
            description = "Delete a movie by its ID"
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.WRITE_SCOPE_MOVIE})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted movie"
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
                            description = "Movie not found",
                            content = @Content(mediaType = "application/problem+json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    ),
            }
    )
    public ResponseEntity<Void> deleteMovie(@PathVariable UUID id) {
        movieFacade.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }
}
