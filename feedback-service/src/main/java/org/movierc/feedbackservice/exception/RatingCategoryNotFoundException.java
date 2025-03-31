package org.movierc.feedbackservice.exception;

import java.util.UUID;

public class RatingCategoryNotFoundException extends RuntimeException {
    public RatingCategoryNotFoundException(UUID id) {
        super("Rating category not found with id: " + id);
    }
}
