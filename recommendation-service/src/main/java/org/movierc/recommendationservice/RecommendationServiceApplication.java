package org.movierc.recommendationservice;

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
public class RecommendationServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(RecommendationServiceApplication.class, args);
  }

  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
            .authorizeHttpRequests(x -> x
                    .requestMatchers(HttpMethod.GET, "/api/v1/**").hasAuthority(Authorities.READ_ALL)
                    .anyRequest().permitAll()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(Customizer.withDefaults()));
    return http.build();
  }
}
