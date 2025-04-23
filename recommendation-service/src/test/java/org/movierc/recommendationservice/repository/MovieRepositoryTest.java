package org.movierc.recommendationservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

import org.movierc.recommendationservice.entity.AverageMovieRating;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.movierc.recommendationservice.entity.RatingDetail;
import org.movierc.recommendationservice.entity.UserMovieRating;

@DataJpaTest
class MovieRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    private RatingCategory c1, c2, c3;
    private Movie m1, m2, m3, m4;

    @BeforeEach
    void setUp() {
        c1 = new RatingCategory(); c1.setName("Plot"); entityManager.persist(c1);
        c2 = new RatingCategory(); c2.setName("Cinematography"); entityManager.persist(c2);
        c3 = new RatingCategory(); c3.setName("Soundtrack"); entityManager.persist(c3);
        entityManager.flush();

        m1 = new Movie(); m1.setTitle("The Shawshank Redemption"); entityManager.persist(m1);
        m2 = new Movie(); m2.setTitle("The Godfather"); entityManager.persist(m2);
        m3 = new Movie(); m3.setTitle("The Dark Knight"); entityManager.persist(m3);
        m4 = new Movie(); m4.setTitle("Pulp Fiction"); entityManager.persist(m4);
        entityManager.flush();

        List<UUID> userIds = List.of(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        Map<UUID, Map<Movie, List<Integer>>> ratings = new HashMap<>();
        ratings.put(userIds.get(0), Map.of(
                m1, List.of(80, 85, 90),
                m2, List.of(90, 95, 100),
                m3, List.of(100, 100, 100),
                m4, List.of(70, 75, 80)
        ));
        ratings.put(userIds.get(1), Map.of(
                m1, List.of(60, 65, 70),
                m2, List.of(70, 75, 80),
                m3, List.of(80, 85, 90),
                m4, List.of(90, 95, 100)
        ));
        ratings.put(userIds.get(2), Map.of(
                m1, List.of(50, 55, 60),
                m2, List.of(40, 55, 30),
                m3, List.of(70, 75, 80),
                m4, List.of(80, 85, 90)
        ));

        for (Map.Entry<UUID, Map<Movie, List<Integer>>> userEntry : ratings.entrySet()) {
            UUID userId = userEntry.getKey();
            for (Map.Entry<Movie, List<Integer>> movieEntry : userEntry.getValue().entrySet()) {
                Movie movie = movieEntry.getKey();
                List<Integer> vals = movieEntry.getValue();

                UserMovieRating umr = new UserMovieRating();
                umr.setUserId(userId);
                umr.setMovie(movie);
                entityManager.persist(umr);
                entityManager.flush();

                RatingDetail rd1 = new RatingDetail();
                rd1.setRating(vals.get(0)); rd1.setRatingCategory(c1); rd1.setUserMovieRating(umr);
                entityManager.persist(rd1);
                RatingDetail rd2 = new RatingDetail();
                rd2.setRating(vals.get(1)); rd2.setRatingCategory(c2); rd2.setUserMovieRating(umr);
                entityManager.persist(rd2);
                RatingDetail rd3 = new RatingDetail();
                rd3.setRating(vals.get(2)); rd3.setRatingCategory(c3); rd3.setUserMovieRating(umr);
                entityManager.persist(rd3);
                entityManager.flush();
            }
        }
    }

    @Test
    void findRecommendationsByCategoriesAboveThreshold_AllCategoriesAboveThreshold80() {
        Set<UUID> categories = Set.of(c1.getId(), c2.getId(), c3.getId());
        var result = movieRepository.findRecommendationsByCategoriesAboveThreshold(categories, 80, PageRequest.of(0, 10));
        assertEquals(2, result.size());
        assertEquals(m3.getId(), result.get(0).getId());
        assertEquals(m4.getId(), result.get(1).getId());
    }

    @Test
    void findRecommendationsByCategoriesAboveThreshold_Threshold0Pagination() {
        Set<UUID> categories = Set.of(c1.getId(), c2.getId(), c3.getId());
        var result = movieRepository.findRecommendationsByCategoriesAboveThreshold(categories, 0, PageRequest.of(0, 2));
        assertEquals(2, result.size());
        assertEquals(m3.getId(), result.get(0).getId());
        assertEquals(m4.getId(), result.get(1).getId());
    }

    @Test
    void findRecommendationsByCategoriesAboveThreshold_EmptyCategories() {
        var result = movieRepository.findRecommendationsByCategoriesAboveThreshold(Set.of(), 0, PageRequest.of(0, 10));
        assertTrue(result.isEmpty());
    }

    @Test
    void findSimilarMovies_ForMovie1() {
        var result = movieRepository.findSimilarMovies(m1.getId(), PageRequest.of(0, 10));
        assertEquals(3, result.size());
        assertEquals(m3.getId(), result.get(0).getId());
        assertEquals(m4.getId(), result.get(1).getId());
        assertEquals(m2.getId(), result.get(2).getId());
    }

    @Test
    void findSimilarMovies_Pagination() {
        var result = movieRepository.findSimilarMovies(m1.getId(), PageRequest.of(0, 2));
        assertEquals(2, result.size());
        assertEquals(m3.getId(), result.get(0).getId());
        assertEquals(m4.getId(), result.get(1).getId());
    }

    @Test
    void findAverageMovieRatingsByMovieIdIn_ForMovies1And2() {
        Set<UUID> movieIds = Set.of(m1.getId(), m2.getId());
        List<AverageMovieRating> averages = movieRepository.findAverageMovieRatingsByMovieIdIn(movieIds, PageRequest.of(0, 10));
        assertEquals(6, averages.size());
        Map<UUID, Map<String, Double>> map = new HashMap<>();
        for (AverageMovieRating am : averages) {
            map.computeIfAbsent(am.movieId(), k -> new HashMap<>())
               .put(am.ratingCategoryName(), am.averageRating());
        }
        assertTrue(map.containsKey(m1.getId()));
        assertTrue(map.containsKey(m2.getId()));

        assertEquals((80 + 60 + 50) / 3.0, map.get(m1.getId()).get(c1.getName()), 1e-6);
        assertEquals((95 + 75 + 55) / 3.0, map.get(m2.getId()).get(c2.getName()), 1e-6);
        assertEquals((100 + 80 + 30) / 3.0, map.get(m2.getId()).get(c3.getName()), 1e-6);
    }
}