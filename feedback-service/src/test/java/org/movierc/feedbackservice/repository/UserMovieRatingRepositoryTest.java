package org.movierc.feedbackservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.movierc.feedbackservice.entity.Movie;
import org.movierc.feedbackservice.entity.User;
import org.movierc.feedbackservice.entity.UserMovieRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserMovieRatingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserMovieRatingRepository userMovieRatingRepository;

    private User user1, user2;
    private Movie movie1, movie2;

    @BeforeEach
    void setUp() {
        // Create and persist User entities
        user1 = User.builder().id(UUID.randomUUID()).build();
        entityManager.persist(user1);

        user2 = User.builder().id(UUID.randomUUID()).build();
        entityManager.persist(user2);

        // Create and persist Movie entities
        movie1 = Movie.builder().id(UUID.randomUUID()).title("Movie 1").build();
        entityManager.persist(movie1);

        movie2 = Movie.builder().id(UUID.randomUUID()).title("Movie 2").build();
        entityManager.persist(movie2);

        entityManager.flush();
        // Create and persist UserMovieRating entities
        UserMovieRating umr1 = UserMovieRating.builder()
                .user(user1)
                .movie(movie1)
                .build();
        entityManager.persist(umr1);

        UserMovieRating umr2 = UserMovieRating.builder()
                .user(user1)
                .movie(movie2)
                .build();
        entityManager.persist(umr2);

        UserMovieRating umr3 = UserMovieRating.builder()
                .user(user2)
                .movie(movie1)
                .build();
        entityManager.persist(umr3);

        entityManager.flush();
    }

    @Test
    void findByUserId_ShouldReturnRatingsForUser() {
        List<UserMovieRating> ratings = userMovieRatingRepository.findByUserId(user1.getId());
        assertEquals(2, ratings.size());
        assertTrue(ratings.stream().allMatch(r -> r.getUser().getId().equals(user1.getId())));
    }

    @Test
    void findByMovieId_ShouldReturnRatingsForMovie() {
        List<UserMovieRating> ratings = userMovieRatingRepository.findByMovieId(movie1.getId());
        assertEquals(2, ratings.size());
        assertTrue(ratings.stream().allMatch(r -> r.getMovie().getId().equals(movie1.getId())));
    }

    @Test
    void findByUserId_NoRatingsForUser() {
        List<UserMovieRating> ratings = userMovieRatingRepository.findByUserId(UUID.randomUUID());
        assertTrue(ratings.isEmpty());
    }

    @Test
    void findByMovieId_NoRatingsForMovie() {
        List<UserMovieRating> ratings = userMovieRatingRepository.findByMovieId(UUID.randomUUID());
        assertTrue(ratings.isEmpty());
    }
}
