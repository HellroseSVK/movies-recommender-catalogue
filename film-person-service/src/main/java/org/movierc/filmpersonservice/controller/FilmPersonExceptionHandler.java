package org.movierc.filmpersonservice.controller;

import org.movierc.filmpersonservice.exception.FilmPersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FilmPersonExceptionHandler {

    @ExceptionHandler(FilmPersonNotFoundException.class)
    public ResponseEntity<?> handleFilmPersonNotFound(FilmPersonNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        pd.setTitle("Film Person Not Found Error");
        pd.setDetail(ex.getMessage());
        return new ResponseEntity<>(pd, HttpStatus.NOT_FOUND);
    }
}
