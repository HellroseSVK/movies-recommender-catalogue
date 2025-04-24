package org.movierc.recommendationservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.UserMovieRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class UserMovieRatingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserMovieRatingRepository userMovieRatingRepository;

    private Movie movie;
    private UUID existingUserId;

    @BeforeEach
    void setUp() {
        movie = Movie.builder()
                .id(UUID.randomUUID())
                .title("Test Movie")
                .build();
        entityManager.persist(movie);

        existingUserId = UUID.randomUUID();
        UserMovieRating umr = UserMovieRating.builder()
                .userId(existingUserId)
                .movie(movie)
                .build();
        entityManager.persist(umr);
        entityManager.flush();
    }

    @Test
    void findByUserIdAndMovieId_ExistingRecord_ReturnsRecord() {
        // Act
        Optional<UserMovieRating> result = userMovieRatingRepository.findByUserIdAndMovieId(existingUserId, movie.getId());
        // Verify
        assertTrue(result.isPresent());
        assertEquals(existingUserId, result.get().getUserId());
        assertEquals(movie.getId(), result.get().getMovie().getId());
    }

    @Test
    void findByUserIdAndMovieId_NonExistingRecord_ReturnsEmpty() {
        // Act
        Optional<UserMovieRating> result = userMovieRatingRepository.findByUserIdAndMovieId(UUID.randomUUID(), movie.getId());
        // Verify
        assertFalse(result.isPresent());
    }

    @Test
    void existsByUserIdAndMovieId_ExistingRecord_ReturnsTrue() {
        // Act
        boolean exists = userMovieRatingRepository.existsByUserIdAndMovieId(existingUserId, movie.getId());
        // Verify
        assertTrue(exists);
    }

    @Test
    void existsByUserIdAndMovieId_NonExistingRecord_ReturnsFalse() {
        // Act
        boolean exists = userMovieRatingRepository.existsByUserIdAndMovieId(UUID.randomUUID(), movie.getId());
        // Verify
        assertFalse(exists);
    }

    @Test
    void deleteByUserIdAndMovieId_ExistingRecord_RecordDeleted() {
        // Setup
        assertTrue(userMovieRatingRepository.existsByUserIdAndMovieId(existingUserId, movie.getId()));

        // Act
        userMovieRatingRepository.deleteByUserIdAndMovieId(existingUserId, movie.getId());
        entityManager.flush();

        // Verify
        boolean existsAfterDelete = userMovieRatingRepository.existsByUserIdAndMovieId(existingUserId, movie.getId());
        assertFalse(existsAfterDelete);
    }
}
