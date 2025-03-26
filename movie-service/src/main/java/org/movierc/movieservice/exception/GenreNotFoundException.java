package org.movierc.movieservice.exception;

import java.util.UUID;

public class GenreNotFoundException extends RuntimeException {
    public GenreNotFoundException(UUID genreId) {
        super("Genre not found with id: " + genreId);
    }
}
