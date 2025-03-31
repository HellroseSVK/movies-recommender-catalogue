package org.movierc.feedbackservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.facade.UserMovieRatingFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/rating")
@RestController
@AllArgsConstructor
public class UserMovieRatingController {
    private final UserMovieRatingFacade userMovieRatingFacade;

    @GetMapping
    public ResponseEntity<List<UserMovieRatingDto>> getAllRatings() {
        return ResponseEntity.ok(userMovieRatingFacade.getAllRatings());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserMovieRatingDto>> getRatingsByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(userMovieRatingFacade.getRatingsByUserId(userId));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<UserMovieRatingDto>> getRatingsByMovieId(@PathVariable UUID movieId) {
        return ResponseEntity.ok(userMovieRatingFacade.getRatingsByMovieId(movieId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserMovieRatingDto> getRatingById(@PathVariable UUID id) {
        return ResponseEntity.ok(userMovieRatingFacade.getRatingById(id));
    }

    @PostMapping
    public ResponseEntity<UserMovieRatingDto> createRating(@RequestBody @Valid UserMovieRatingCreateDto userMovieRatingCreateDto) {
        UserMovieRatingDto createdUserMovieRating = userMovieRatingFacade.createRating(userMovieRatingCreateDto);
        return new ResponseEntity<>(createdUserMovieRating, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserMovieRatingDto> updateRating(@PathVariable UUID id, @RequestBody UserMovieRatingUpdateDto userMovieRatingUpdateDto) {
        return ResponseEntity.ok(userMovieRatingFacade.updateRating(id, userMovieRatingUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable UUID id) {
        userMovieRatingFacade.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
