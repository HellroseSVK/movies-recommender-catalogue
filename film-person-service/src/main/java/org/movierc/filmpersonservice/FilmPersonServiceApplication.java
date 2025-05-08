package org.movierc.filmpersonservice;

import org.movierc.shared.config.SharedConfig;
import org.movierc.shared.security.Authorities;
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
public class FilmPersonServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilmPersonServiceApplication.class, args);
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(x -> x
						.requestMatchers(HttpMethod.GET, "/api/v1/**").hasAuthority(Authorities.READ_ALL)
						.requestMatchers(HttpMethod.POST, "/api/v1/**").hasAnyAuthority(Authorities.WRITE_ALL_ADMIN, Authorities.WRITE_SCOPE_FILM_PERSON)
						.requestMatchers(HttpMethod.PUT, "/api/v1/**").hasAnyAuthority(Authorities.WRITE_ALL_ADMIN, Authorities.WRITE_SCOPE_FILM_PERSON)
						.requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasAnyAuthority(Authorities.WRITE_ALL_ADMIN, Authorities.WRITE_SCOPE_FILM_PERSON)
						.anyRequest().permitAll()
				)
				.oauth2ResourceServer(oauth2 -> oauth2.opaqueToken(Customizer.withDefaults()));
		return http.build();
	}
}
