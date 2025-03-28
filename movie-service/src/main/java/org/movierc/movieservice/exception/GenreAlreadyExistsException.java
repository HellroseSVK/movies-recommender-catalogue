package org.movierc.movieservice.exception;

import org.movierc.movieservice.entity.Genre;

public class GenreAlreadyExistsException extends RuntimeException {
    public GenreAlreadyExistsException(Genre genre) {
        super("Genre with name " + genre.getName() + " already exists and has id " + genre.getId());
    }
}
