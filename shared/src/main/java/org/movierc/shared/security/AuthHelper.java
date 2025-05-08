package org.movierc.shared.security;

import org.springframework.security.core.Authentication;

public class AuthHelper {
    /**
     * Check if the user is the same as the authenticated user or has one of the given scopes.
     *
     * @param authentication the authentication to check
     * @param userId the user id to check against
     * @param scopes the scopes to check for
     * @return true if the user is the same or has one of the scopes, false otherwise
     */
    public static boolean isOrHas(Authentication authentication, String userId, String... scopes) {
        if (authentication == null) {
            return false;
        }

        if (authentication.getName().equals(userId)) {
            return true;
        }

        if (scopes != null) {
            for (String scope : scopes) {
                if (authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(scope))
                ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Check if the user has one of the given scopes and return the username if not.
     * @param authentication the authentication to check
     * @param scopes the scopes to check for
     * @return the username if the user does not have any of the scopes, null otherwise
     */
    public static String getNameUnlessHas(Authentication authentication, String... scopes) {
        if (authentication == null) {
            throw new IllegalArgumentException("Authentication cannot be null");
        }

        if (scopes != null) {
            for (String scope : scopes) {
                if (authentication
                        .getAuthorities()
                        .stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(scope))
                ) {
                    return null;
                }
            }
        }

        return authentication.getName();
    }
}
