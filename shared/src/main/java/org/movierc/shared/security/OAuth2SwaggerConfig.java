package org.movierc.shared.security;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.movierc.shared.security.OAuth2Scopes.RATING_SCOPE;
import static org.movierc.shared.security.OAuth2Scopes.READ_SCOPE;
import static org.movierc.shared.security.OAuth2Scopes.WRITE_SCOPE_ADMIN;
import static org.movierc.shared.security.OAuth2Scopes.WRITE_SCOPE_FILM_PERSON;
import static org.movierc.shared.security.OAuth2Scopes.WRITE_SCOPE_MOVIE;
import static org.movierc.shared.security.OAuth2Scopes.WRITE_SCOPE_RATING;

@Configuration
public class OAuth2SwaggerConfig {
    @Bean
    public Components securitySchemeComponent() {
        return new Components().addSecuritySchemes(
                OAuth2.SECURITY_SCHEME,
                new io.swagger.v3.oas.models.security.SecurityScheme()
                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.OAUTH2)
                        .description("get access token with OAuth 2 Authorization Code Grant")
                        .flows(new OAuthFlows()
                                .authorizationCode(new OAuthFlow()
                                        .authorizationUrl("https://id.muni.cz/oidc/authorize")
                                        .tokenUrl("https://id.muni.cz/oidc/token")
                                        .scopes(new Scopes()
                                                .addString("openid", "get user identifier")
                                                .addString(READ_SCOPE, "Read access to all services")
                                                .addString(WRITE_SCOPE_ADMIN, "Write access to all services")
                                                .addString(WRITE_SCOPE_MOVIE, "Write access to movie service")
                                                .addString(WRITE_SCOPE_FILM_PERSON, "Write access to film person service")
                                                .addString(WRITE_SCOPE_RATING, "Write access to rating service")
                                                .addString(RATING_SCOPE, "Ability to rate movies")
                                        )
                                )
                        )
        );
    }
}
