package org.movierc.feedbackservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.facade.UserMovieRatingFacade;
import org.movierc.shared.security.AuthHelper;
import org.movierc.shared.security.Authorities;
import org.movierc.shared.security.OAuth2;
import org.movierc.shared.security.OAuth2Scopes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

@RequestMapping("/api/v1/rating")
@RestController
@AllArgsConstructor
public class UserMovieRatingController {
    private final UserMovieRatingFacade userMovieRatingFacade;

    @GetMapping
    @Operation(
            summary = "Get all user movie ratings",
            description = "Get a list of all user movie ratings. Each rating consist of different categories, " +
                    "which are used to rate movies."
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.READ_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User movie ratings retrieved successfully"),
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
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<List<UserMovieRatingDto>> getAllRatings() {
        return ResponseEntity.ok(userMovieRatingFacade.getAllRatings());
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get user movie ratings by user ID",
            description = "Get a list of all user movie ratings by user ID. Each rating consist of different categories, " +
                    "which are used to rate movies."
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.READ_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User movie ratings retrieved successfully"),
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
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/problem+json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<List<UserMovieRatingDto>> getRatingsByUserId(
            @PathVariable String userId,
            Authentication authentication
    ) {
        if (!AuthHelper.isOrHas(authentication, userId, Authorities.WRITE_ALL_ADMIN)) {
            return ResponseEntity.ok(userMovieRatingFacade.getRatingsByUserId(userId));
        }

        return ResponseEntity.ok(userMovieRatingFacade.getRatingsByUserId(userId));
    }

    @GetMapping("/movie/{movieId}")
    @Operation(
            summary = "Get user movie ratings by movie ID",
            description = "Get a list of all user movie ratings by movie ID. Each rating consist of different categories, " +
                    "which are used to rate movies."
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.READ_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User movie ratings retrieved successfully"),
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
    public ResponseEntity<List<UserMovieRatingDto>> getRatingsByMovieId(@PathVariable UUID movieId) {
        return ResponseEntity.ok(userMovieRatingFacade.getRatingsByMovieId(movieId));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get user movie rating by ID",
            description = "Get a user movie rating by ID. Each rating consist of different categories, " +
                    "which are used to rate movies."
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.READ_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User movie rating retrieved successfully"),
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
                    @ApiResponse(responseCode = "404", description = "User movie rating not found", content = @Content(mediaType = "application/problem+json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<UserMovieRatingDto> getRatingById(@PathVariable UUID id) {
        return ResponseEntity.ok(userMovieRatingFacade.getRatingById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create a new user movie rating",
            description = "Create a new user movie rating. Each rating consist of different categories, " +
                    "which are used to rate movies."
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.RATING_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "201", description = "User movie rating created successfully"),
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
                    @ApiResponse(responseCode = "404", description = "User or movie not found", content = @Content(mediaType = "application/problem+json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<UserMovieRatingDto> createRating(
            @RequestBody @Valid UserMovieRatingCreateDto userMovieRatingCreateDto,
            Authentication authentication
    ) {
        if (!AuthHelper.isOrHas(authentication, userMovieRatingCreateDto.getUserId(), Authorities.WRITE_ALL_ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        UserMovieRatingDto createdUserMovieRating = userMovieRatingFacade.createRating(userMovieRatingCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserMovieRating);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a user movie rating",
            description = "Update a user movie rating. Each rating consist of different categories, " +
                    "which are used to rate movies."
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.RATING_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User movie rating updated successfully"),
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
                    @ApiResponse(responseCode = "404", description = "User or movie or user movie rating not found", content = @Content(mediaType = "application/problem+json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<UserMovieRatingDto> updateRating(
            @PathVariable UUID id,
            @RequestBody UserMovieRatingUpdateDto userMovieRatingUpdateDto,
            Authentication authentication
    ) {
        UserMovieRatingDto userMovieRating = userMovieRatingFacade.updateRating(
                id,
                userMovieRatingUpdateDto,
                AuthHelper.getNameUnlessHas(authentication, Authorities.WRITE_ALL_ADMIN)
        );
        return ResponseEntity.status(HttpStatus.OK).body(userMovieRating);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a user movie rating",
            description = "Delete a user movie rating by ID. Each rating consist of different categories, " +
                    "which are used to rate movies."
    )
    @SecurityRequirement(name = OAuth2.SECURITY_SCHEME, scopes = {OAuth2Scopes.RATING_SCOPE})
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "204", description = "User movie rating deleted successfully"),
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
                    @ApiResponse(responseCode = "404", description = "User movie rating not found", content = @Content(mediaType = "application/problem+json")),
                    @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(mediaType = "application/json"))
            }
    )
    public ResponseEntity<Void> deleteRating(
            @PathVariable UUID id,
            Authentication authentication
    ) {
        userMovieRatingFacade.deleteRating(
                id,
                AuthHelper.getNameUnlessHas(authentication, Authorities.WRITE_ALL_ADMIN)
        );
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
