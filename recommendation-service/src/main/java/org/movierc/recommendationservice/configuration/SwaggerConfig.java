package org.movierc.recommendationservice.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi moviesGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("Movie API V1")
                .pathsToMatch("/api/v1/**")
                .build();
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().info(new Info().title("Movie Recommender Catalogue - Recommendation service"));
    }
}
