package org.movierc.movieservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.facade.GenreFacade;
import org.movierc.shared.security.OAuth2;
import org.movierc.shared.security.OAuth2Scopes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/genre")
@AllArgsConstructor
public class GenreController {
    private final GenreFacade genreFacade;

    @GetMapping
    @Operation(
            summary = "Get all genres",
            description = "Retrieve a list of all genres"
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.READ_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of genres"
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
    public ResponseEntity<List<GenreDto>> getGenres() {
        return ResponseEntity.ok(genreFacade.getGenres());
    }

    @PostMapping
    @Operation(
            summary = "Create a new genre",
            description = "Create a new genre"
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.WRITE_SCOPE_MOVIE})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created genre"
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
    public ResponseEntity<GenreDto> createGenre(@RequestBody @Valid GenreCreateDto genreCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreFacade.createGenre(genreCreateDto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a genre",
            description = "Delete a genre by its ID"
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.WRITE_SCOPE_MOVIE})
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "204",
                            description = "Successfully deleted genre"
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
                            description = "Genre not found",
                            content = @Content(mediaType = "application/problem+json")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "application/json")
                    ),
            }
    )
    public ResponseEntity<Void> deleteGenre(@PathVariable UUID id) {
        genreFacade.deleteGenre(id);
        return ResponseEntity.noContent().build();
    }
}
