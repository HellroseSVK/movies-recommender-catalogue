package org.movierc.filmpersonservice.controller;

import org.movierc.filmpersonservice.exception.FilmPersonNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class FilmPersonExceptionHandler {

    @ExceptionHandler(FilmPersonNotFoundException.class)
    public ResponseEntity<?> handleFilmPersonNotFound(FilmPersonNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}