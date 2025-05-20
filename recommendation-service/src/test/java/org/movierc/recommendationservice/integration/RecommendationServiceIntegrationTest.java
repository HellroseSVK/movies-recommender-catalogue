package org.movierc.recommendationservice.integration;


import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.rating.RatingCategoryCreatedEvent;
import org.movierc.recommendationservice.entity.RatingDetail;
import org.movierc.recommendationservice.entity.UserMovieRating;
import org.movierc.recommendationservice.rabbit.MovieEventConsumer;
import org.movierc.recommendationservice.rabbit.RatingCategoryEventConsumer;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.movierc.recommendationservice.repository.UserMovieRatingRepository;
import org.movierc.shared.security.Authorities;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("without-artemis")
public class RecommendationServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MovieEventConsumer movieEventConsumer;

    @Autowired
    private RatingCategoryEventConsumer ratingCategoryEventConsumer;

    @Autowired
    private UserMovieRatingRepository userMovieRatingRepository;

    @Autowired
    private RatingCategoryRepository ratingCategoryRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void getRecommendationsByCategoriesAboveThreshold_NoRatings_ReturnsEmptyArray() throws Exception {
        // Setup
        var dummyCategory = RatingCategoryCreatedEvent.builder()
                .id(UUID.randomUUID())
                .name("Category")
                .build();

        ratingCategoryEventConsumer.handleRatingCategoryCreatedEvent(dummyCategory);

        // Act
        mockMvc.perform(get("/api/v1/recommendation/by-categories/0")
                        .param("categoryIds", dummyCategory.getId().toString())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

    }

    @Test
    void getRecommendationsByCategoriesAboveThreshold_MovieWithRating_ReturnsMovie() throws Exception {
        // Setup
        var dummyMovie = MovieCreatedEvent.builder()
                .id(UUID.randomUUID())
                .title("Dummy Movie")
                .build();
        var userId = UUID.randomUUID().toString();

        var dummyCategory = RatingCategoryCreatedEvent.builder()
                .id(UUID.randomUUID())
                .name("Category")
                .build();

        movieEventConsumer.handleMovieCreatedEvent(dummyMovie);
        ratingCategoryEventConsumer.handleRatingCategoryCreatedEvent(dummyCategory);

        var movie = movieRepository.findById(dummyMovie.getId()).get();
        var category = ratingCategoryRepository.findById(dummyCategory.getId()).get();

        var rds = List.of(
                RatingDetail.builder()
                        .ratingCategory(category)
                        .rating(50)
                        .build()
        );

        var rating = userMovieRatingRepository.save(
                UserMovieRating.builder()
                        .userId(userId)
                        .ratingDetails(rds)
                        .movie(movie)
                        .build()
        );
        rds.getFirst().setUserMovieRating(rating);
        entityManager.flush();

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation/by-categories/0")
                        .param("categoryIds", dummyCategory.getId().toString())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(dummyMovie.getId().toString()));

        mockMvc.perform(get("/api/v1/recommendation/by-categories/49")
                        .param("categoryIds", dummyCategory.getId().toString())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(dummyMovie.getId().toString()));

        mockMvc.perform(get("/api/v1/recommendation/by-categories/50")
                        .param("categoryIds", dummyCategory.getId().toString())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getRecommendationsByMovieId_OnlyOneMovie_ReturnsEmptyArray() throws Exception {
        // Setup
        var dummyMovie1 = MovieCreatedEvent.builder()
                .id(UUID.randomUUID())
                .title("Dummy Movie1")
                .build();

        movieEventConsumer.handleMovieCreatedEvent(dummyMovie1);

        var movie1 = movieRepository.findById(dummyMovie1.getId()).get();

        entityManager.flush();

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation/by-movie/" + movie1.getId())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void getRecommendationsByMovieId_MovieWithRating_ReturnsOtherMovie() throws Exception {
        // Setup
        var dummyMovie1 = MovieCreatedEvent.builder()
                .id(UUID.randomUUID())
                .title("Dummy Movie1")
                .build();
        var dummyMovie2 = MovieCreatedEvent.builder()
                .id(UUID.randomUUID())
                .title("Dummy Movie2")
                .build();
        var dummyMovie3 = MovieCreatedEvent.builder()
                .id(UUID.randomUUID())
                .title("Dummy Movie3")
                .build();
        var userId = UUID.randomUUID().toString();

        var dummyCategory = RatingCategoryCreatedEvent.builder()
                .id(UUID.randomUUID())
                .name("Category")
                .build();

        movieEventConsumer.handleMovieCreatedEvent(dummyMovie1);
        movieEventConsumer.handleMovieCreatedEvent(dummyMovie2);
        movieEventConsumer.handleMovieCreatedEvent(dummyMovie3);
        ratingCategoryEventConsumer.handleRatingCategoryCreatedEvent(dummyCategory);

        var movie1 = movieRepository.findById(dummyMovie1.getId()).get();
        var movie2 = movieRepository.findById(dummyMovie2.getId()).get();
        var movie3 = movieRepository.findById(dummyMovie3.getId()).get();
        var category = ratingCategoryRepository.findById(dummyCategory.getId()).get();

        var rds1 = List.of(
                RatingDetail.builder()
                        .ratingCategory(category)
                        .rating(50)
                        .build()
        );

        var rating1 = userMovieRatingRepository.save(
                UserMovieRating.builder()
                        .userId(userId)
                        .ratingDetails(rds1)
                        .movie(movie1)
                        .build()
        );
        rds1.getFirst().setUserMovieRating(rating1);

        var rds2 = List.of(
                RatingDetail.builder()
                        .ratingCategory(category)
                        .rating(50)
                        .build()
        );

        var rating2 = userMovieRatingRepository.save(
                UserMovieRating.builder()
                        .userId(userId)
                        .ratingDetails(rds2)
                        .movie(movie2)
                        .build()
        );
        rds2.getFirst().setUserMovieRating(rating2);
        entityManager.flush();

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation/by-movie/" + movie1.getId())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(movie2.getId().toString()));

        mockMvc.perform(get("/api/v1/recommendation/by-movie/" + movie3.getId())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
