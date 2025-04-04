package org.movierc.feedbackservice.controller;

import org.movierc.feedbackservice.exception.UserMovieRatingNotFoundException;
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
    
    @ExceptionHandler(org.movierc.feedbackservice.exception.UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFound(org.movierc.feedbackservice.exception.UserNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("User Not Found Error");
        pd.setDetail(ex.getMessage());
        return new ResponseEntity<>(pd, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(org.movierc.feedbackservice.exception.MovieNotFoundException.class)
    public ResponseEntity<?> handleMovieNotFound(org.movierc.feedbackservice.exception.MovieNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Movie Not Found Error");
        pd.setDetail(ex.getMessage());
        return new ResponseEntity<>(pd, HttpStatus.NOT_FOUND);
    }
}
