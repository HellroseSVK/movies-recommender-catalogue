package org.movierc.movieservice.exception;

import java.util.UUID;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException(UUID movieId) {
        super("Movie not found with id: " + movieId);
    }
}
