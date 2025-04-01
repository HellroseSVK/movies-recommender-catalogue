package org.movierc.recommendationservice.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID categoryId) {
        super("Category not found with id: " + categoryId);
    }
}
