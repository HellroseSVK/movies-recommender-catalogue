package org.movierc.feedbackservice.controller;

import org.movierc.feedbackservice.exception.MovieNotFoundException;
import org.movierc.feedbackservice.exception.RatingCategoryNotFoundException;
import org.movierc.feedbackservice.exception.UserMovieRatingNotFoundException;
import org.movierc.feedbackservice.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FeedbackExceptionHandler {
    @ExceptionHandler(UserMovieRatingNotFoundException.class)
    public ResponseEntity<?> handleUserMovieRatingNotFound(UserMovieRatingNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("User Movie Rating Not Found Error");
        pd.setDetail(ex.getMessage());
        return new ResponseEntity<>(pd, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("User Not Found Error");
        pd.setDetail(ex.getMessage());
        return new ResponseEntity<>(pd, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(MovieNotFoundException.class)
    public ResponseEntity<?> handleMovieNotFound(MovieNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Movie Not Found Error");
        pd.setDetail(ex.getMessage());
        return new ResponseEntity<>(pd, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RatingCategoryNotFoundException.class)
    public ResponseEntity<?> handleRatingCategoryNotFound(RatingCategoryNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Rating Category Not Found Error");
        pd.setDetail(ex.getMessage());
        return new ResponseEntity<>(pd, HttpStatus.NOT_FOUND);
    }
}
