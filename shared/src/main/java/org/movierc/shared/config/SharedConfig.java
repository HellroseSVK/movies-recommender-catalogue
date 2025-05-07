package org.movierc.shared.config;

import org.movierc.shared.security.OAuth2SwaggerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({OAuth2SwaggerConfig.class})
@ComponentScan(basePackages = "org.movierc.shared")
public class SharedConfig {
    @Bean
    public String rabbitExchangeName() {
        return "movierc-exchange";
    }
}
