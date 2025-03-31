package org.movierc.userservice.exception;

import org.movierc.userservice.entity.User;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(User user) {
        super("User with username " + user.getUsername() + " already exists");
    }
}
