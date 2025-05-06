package org.movierc.userservice.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    private static final String SECURITY_SCHEME_OAUTH2 = "MUNI";

    @Bean
    public GroupedOpenApi moviesGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("Movies API V1")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("Movie Recommender Catalogue - User service"))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_OAUTH2,
                                new io.swagger.v3.oas.models.security.SecurityScheme()
                                        .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.OAUTH2)
                                        .description("get access token with OAuth 2 Authorization Code Grant")
                                        .flows(new OAuthFlows()
                                                .authorizationCode(new OAuthFlow()
                                                        .authorizationUrl("https://id.muni.cz/oidc/authorize")
                                                        .tokenUrl("https://id.muni.cz/oidc/token")
                                                        .scopes(new Scopes()
                                                                .addString("openid", "get user identifier")
                                                                .addString("test_read", "read user data")
                                                                .addString("test_write", "create user")
                                                                .addString("test_1", "update user")
                                                                .addString("test_2", "delete user")
                                                        )
                                                )
                                        )
                        )
                );
    }
}
