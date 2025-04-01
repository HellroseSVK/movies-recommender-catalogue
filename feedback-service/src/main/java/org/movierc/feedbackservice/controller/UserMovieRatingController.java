package org.movierc.feedbackservice.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.facade.UserMovieRatingFacade;
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
    public ResponseEntity<List<UserMovieRatingDto>> getRatingsByUserId(@PathVariable(name = "userId") UUID userId) {
        return ResponseEntity.ok(userMovieRatingFacade.getRatingsByUserId(userId));
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<UserMovieRatingDto>> getRatingsByMovieId(@PathVariable(name = "movieId") UUID movieId) {
        return ResponseEntity.ok(userMovieRatingFacade.getRatingsByMovieId(movieId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserMovieRatingDto> getRatingById(@PathVariable(name = "id") UUID id) {
        return ResponseEntity.ok(userMovieRatingFacade.getRatingById(id));
    }

    @PostMapping
    public ResponseEntity<UserMovieRatingDto> createRating(@RequestBody @Valid UserMovieRatingCreateDto userMovieRatingCreateDto) {
        UserMovieRatingDto createdUserMovieRating = userMovieRatingFacade.createRating(userMovieRatingCreateDto);
        return new ResponseEntity<>(createdUserMovieRating, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserMovieRatingDto> updateRating(@PathVariable(name = "id") UUID id, @RequestBody UserMovieRatingUpdateDto userMovieRatingUpdateDto) {
        return ResponseEntity.ok(userMovieRatingFacade.updateRating(id, userMovieRatingUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable(name = "id") UUID id) {
        userMovieRatingFacade.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
