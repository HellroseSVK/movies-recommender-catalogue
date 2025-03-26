package org.movierc.movieservice.exception;

import java.util.UUID;

public class FilmPersonNotFoundException extends RuntimeException {
    public FilmPersonNotFoundException(UUID filmPersonId) {
        super("Film person not found with id: " + filmPersonId);
    }
}
