package org.movierc.shared.security;

public class Authorities {
    private static final String SCOPE_PREFIX = "SCOPE_";

    public static final String READ_ALL = SCOPE_PREFIX + OAuth2Scopes.READ_SCOPE;
    public static final String WRITE_ALL_ADMIN = SCOPE_PREFIX + OAuth2Scopes.WRITE_SCOPE_ADMIN;
    public static final String WRITE_SCOPE_MOVIE = SCOPE_PREFIX + OAuth2Scopes.WRITE_SCOPE_MOVIE;
    public static final String WRITE_SCOPE_FILM_PERSON = SCOPE_PREFIX + OAuth2Scopes.WRITE_SCOPE_FILM_PERSON;
    public static final String WRITE_SCOPE_RATING = SCOPE_PREFIX + OAuth2Scopes.WRITE_SCOPE_RATING;
    public static final String RATING_SCOPE = SCOPE_PREFIX + OAuth2Scopes.RATING_SCOPE;
}
