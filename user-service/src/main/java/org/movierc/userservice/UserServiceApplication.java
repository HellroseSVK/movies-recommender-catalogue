package org.movierc.userservice;

import org.movierc.shared.config.SharedConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@SpringBootApplication
@Import({SharedConfig.class})
public class UserServiceApplication {
    private static final String SECURITY_SCHEME_OAUTH2 = "MUNI";
    public static final String SECURITY_SCHEME_NAME = SECURITY_SCHEME_OAUTH2;

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(x -> x
                        .requestMatchers(HttpMethod.GET, "/api/v1/user/**").hasAuthority("SCOPE_test_read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/user/**").hasAuthority("SCOPE_test_write")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/user/**").hasAuthority("SCOPE_test_1")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/user/**").hasAuthority("SCOPE_test_2")
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2-> oauth2.opaqueToken(Customizer.withDefaults()))
        ;
        return http.build();
    }
}
