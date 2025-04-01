package org.movierc.recommendationservice.controller;

import org.movierc.recommendationservice.exception.CategoryNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RecommendationExceptionHandler {
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<?> handleUserMovieRatingNotFound(CategoryNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
