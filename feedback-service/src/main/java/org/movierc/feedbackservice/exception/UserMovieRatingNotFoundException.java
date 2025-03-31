package org.movierc.feedbackservice.exception;

import java.util.UUID;

public class UserMovieRatingNotFoundException extends RuntimeException {
    public UserMovieRatingNotFoundException(UUID userMovieRatingId) {
        super("User movie rating not found with id: " + userMovieRatingId);
    }
}
