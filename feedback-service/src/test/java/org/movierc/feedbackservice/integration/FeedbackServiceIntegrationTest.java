package org.movierc.feedbackservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.user.UserCreatedEvent;
import org.movierc.feedbackservice.dto.RatingDetailCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.entity.Movie;
import org.movierc.feedbackservice.entity.RatingCategory;
import org.movierc.feedbackservice.entity.RatingDetail;
import org.movierc.feedbackservice.entity.User;
import org.movierc.feedbackservice.entity.UserMovieRating;
import org.movierc.feedbackservice.rabbit.MovieEventConsumer;
import org.movierc.feedbackservice.rabbit.UserEventConsumer;
import org.movierc.feedbackservice.rabbit.UserMovieRatingEventProducer;
import org.movierc.feedbackservice.repository.RatingCategoryRepository;
import org.movierc.feedbackservice.repository.UserMovieRatingRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("without-artemis")
class FeedbackServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMovieRatingRepository userMovieRatingRepository;

    @Autowired
    private RatingCategoryRepository ratingCategoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieEventConsumer movieEventConsumer;

    @Autowired
    private UserEventConsumer userEventConsumer;

    @MockitoBean
    private UserMovieRatingEventProducer userMovieRatingEventProducer;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    private User user;
    private Movie movie;
    private RatingCategory ratingCategory;

    @BeforeEach
    void setUp() {
        var userId = UUID.randomUUID();
        user = User.builder().id(userId).username("Pavel69").build();
        var movieId = UUID.randomUUID();
        movie = Movie.builder().id(movieId).title("Test Movie").build();
        ratingCategory = RatingCategory.builder().name("Test Category").build();

        var userCreatedEvent = UserCreatedEvent.builder().username("Pavel69").id(userId).build();
        var movieCreatedEvent = MovieCreatedEvent.builder().id(movieId).title("Test Movie").build();

        userEventConsumer.handleUserCreatedEvent(userCreatedEvent);
        movieEventConsumer.handleMovieCreatedEvent(movieCreatedEvent);
        ratingCategory = ratingCategoryRepository.saveAndFlush(ratingCategory);
    }

    @Test
    @Transactional
    void createRating_createsSuccessfully() throws Exception {
        var request = UserMovieRatingCreateDto.builder().movieId(movie.getId()).userId(user.getId())
                .ratingDetails(List.of(RatingDetailCreateDto.builder().rating(45).ratingCategoryId(ratingCategory.getId()).build()))
                .comment("Awesome!")
                .build();

        mockMvc.perform(post("/api/v1/rating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        UserMovieRating rating = userMovieRatingRepository.findByMovieId(movie.getId()).get(0);
        assertEquals(45, rating.getRatingDetails().get(0).getRating());
        verify(userMovieRatingEventProducer).sendMovieRatingCreatedEvent(any());
    }

    @Test
    @Transactional
    void getRatingsByUserId_getsRating() throws Exception {
        UserMovieRating rating = UserMovieRating.builder()
                .user(user)
                .movie(movie)
                .ratingDetails(
                        List.of(RatingDetail.builder().rating(45).ratingCategoryId(ratingCategory.getId()).build())
                )
                .build();
        userMovieRatingRepository.saveAndFlush(rating);

        mockMvc.perform(get("/api/v1/rating/user/" + user.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id").value(user.getId().toString()))
                .andExpect(jsonPath("$[0].movie.id").value(movie.getId().toString()))
                .andExpect(jsonPath("$[0].ratingDetails[0].rating").value(45));
    }

    @Test
    @Transactional
    void updateRating_updatesSuccessfully() throws Exception {
        UserMovieRating rating = UserMovieRating.builder()
                .user(user)
                .movie(movie)
                .ratingDetails(
                        List.of(RatingDetail.builder().rating(45).ratingCategoryId(ratingCategory.getId()).build())
                )
                .comment("Good movie!")
                .build();
        userMovieRatingRepository.saveAndFlush(rating);

        var updateRequest = UserMovieRatingCreateDto.builder()
                .movieId(movie.getId())
                .userId(user.getId())
                .ratingDetails(List.of(RatingDetailCreateDto.builder().rating(50).ratingCategoryId(ratingCategory.getId()).build()))
                .comment("Great movie!")
                .build();

        mockMvc.perform(put("/api/v1/rating/" + rating.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        UserMovieRating updatedRating = userMovieRatingRepository.findByMovieId(movie.getId()).getFirst();
        assertEquals(50, updatedRating.getRatingDetails().getFirst().getRating());
        assertEquals("Great movie!", updatedRating.getComment());
        verify(userMovieRatingEventProducer).sendMovieRatingUpdatedEvent(any());
    }

    @Test
    @Transactional
    void deleteRating_deletesSuccessfully() throws Exception {
        UserMovieRating rating = UserMovieRating.builder()
                .user(user)
                .movie(movie)
                .ratingDetails(
                        List.of(RatingDetail.builder().rating(45).ratingCategoryId(ratingCategory.getId()).build())
                )
                .build();
        userMovieRatingRepository.saveAndFlush(rating);

        mockMvc.perform(delete("/api/v1/rating/" + rating.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rating.getId())))
                .andExpect(status().is(204));

        List<UserMovieRating> ratings = userMovieRatingRepository.findByMovieId(movie.getId());
        assertTrue(ratings.isEmpty());
        verify(userMovieRatingEventProducer).sendMovieRatingDeletedEvent(any());
    }

    @Test
    @Transactional
    void getRatingsByMovieId_getsRatings() throws Exception {
        UserMovieRating rating = UserMovieRating.builder()
                .user(user)
                .movie(movie)
                .ratingDetails(
                        List.of(RatingDetail.builder().rating(45).ratingCategoryId(ratingCategory.getId()).build())
                )
                .build();
        userMovieRatingRepository.saveAndFlush(rating);

        mockMvc.perform(get("/api/v1/rating/movie/" + movie.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].user.id").value(user.getId().toString()))
                .andExpect(jsonPath("$[0].movie.id").value(movie.getId().toString()))
                .andExpect(jsonPath("$[0].ratingDetails[0].rating").value(45));
    }

    @Test
    @Transactional
    void getAllRatings_getsAllRatings() throws Exception {
        UserMovieRating rating1 = UserMovieRating.builder()
                .user(user)
                .movie(movie)
                .ratingDetails(
                        List.of(RatingDetail.builder().rating(45).ratingCategoryId(ratingCategory.getId()).build())
                )
                .build();
        userMovieRatingRepository.saveAndFlush(rating1);

        UserMovieRating rating2 = UserMovieRating.builder()
                .user(user)
                .movie(movie)
                .ratingDetails(
                        List.of(RatingDetail.builder().rating(50).ratingCategoryId(ratingCategory.getId()).build())
                )
                .build();
        userMovieRatingRepository.saveAndFlush(rating2);

        mockMvc.perform(get("/api/v1/rating")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
