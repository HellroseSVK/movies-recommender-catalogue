package org.movierc.feedbackservice;

import org.movierc.shared.config.SharedConfig;
import org.movierc.shared.security.Authorities;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jms.artemis.ArtemisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@Import({SharedConfig.class})
@EnableConfigurationProperties(ArtemisProperties.class)
public class FeedbackServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedbackServiceApplication.class, args);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(x -> x
                        .requestMatchers(HttpMethod.GET, "/api/v1/**").hasAuthority(Authorities.READ_ALL)
                        .requestMatchers(HttpMethod.POST, "/api/v1/rating/category").hasAnyAuthority(Authorities.WRITE_ALL_ADMIN, Authorities.WRITE_SCOPE_RATING)
                        .requestMatchers(HttpMethod.POST, "/api/v1/rating/**").hasAnyAuthority(Authorities.WRITE_ALL_ADMIN, Authorities.RATING_SCOPE)
                        .requestMatchers(HttpMethod.PUT, "/api/v1/rating/**").hasAnyAuthority(Authorities.WRITE_ALL_ADMIN, Authorities.RATING_SCOPE)
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/rating/**").hasAnyAuthority(Authorities.WRITE_ALL_ADMIN, Authorities.RATING_SCOPE)
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(Customizer.withDefaults()));
        return http.build();
    }
}
