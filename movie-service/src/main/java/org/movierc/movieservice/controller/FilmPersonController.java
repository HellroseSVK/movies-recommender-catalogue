package org.movierc.movieservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.movieservice.dto.AssignFilmPersonRoleDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.RemoveFilmPersonRoleDto;
import org.movierc.movieservice.facade.MovieFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequestMapping("/api/v1/movie/{movieId}/filmPerson")
@RestController
@AllArgsConstructor
public class FilmPersonController {
    private final MovieFacade movieFacade;

    @PostMapping
    @Operation(
            summary = "Assign a film person to a movie",
            description = "Assign a film person to a movie"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully assigned film person to movie"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie or film person not found",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Film person already assigned to movie in the same role",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "text/plain")
                    ),
            }
    )
    public ResponseEntity<MovieDto> assignFilmPerson(@PathVariable UUID movieId, @RequestBody @Valid AssignFilmPersonRoleDto assignFilmPersonRoleDto) {
        return ResponseEntity.ok(movieFacade.assignFilmPerson(movieId, assignFilmPersonRoleDto));
    }

    @DeleteMapping
    @Operation(
            summary = "Remove a filmPerson from a movie",
            description = "Remove a filmPerson from a movie"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully removed filmPerson from movie"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Movie or filmPerson not found",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content(mediaType = "text/plain")
                    ),
            }
    )
    public ResponseEntity<MovieDto> removeFilmPerson(@PathVariable UUID movieId, @RequestBody @Valid RemoveFilmPersonRoleDto removeFilmPersonRoleDto) {
        return ResponseEntity.ok(movieFacade.removeFilmPerson(movieId, removeFilmPersonRoleDto));
    }
}
