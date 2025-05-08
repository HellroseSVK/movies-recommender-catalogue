package org.movierc.recommendationservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.movierc.recommendationservice.entity.AverageMovieRating;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.movierc.recommendationservice.entity.RatingDetail;
import org.movierc.recommendationservice.entity.UserMovieRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class MovieRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @MockitoBean
    private SecurityFilterChain securityFilterChain;

    private RatingCategory c1, c2, c3;
    private Movie m1, m2, m3, m4;

    @BeforeEach
    void setUp() {
        c1 = RatingCategory.builder()
                .id(UUID.randomUUID())
                .name("Plot")
                .build();
        entityManager.persist(c1);
        c2 = RatingCategory.builder()
                .id(UUID.randomUUID())
                .name("Cinematography")
                .build();
        entityManager.persist(c2);
        c3 = RatingCategory.builder()
                .id(UUID.randomUUID())
                .name("Soundtrack")
                .build();
        entityManager.persist(c3);
        entityManager.flush();

        m1 = Movie.builder()
                .id(UUID.randomUUID())
                .title("The Shawshank Redemption")
                .build();
        entityManager.persist(m1);
        m2 = Movie.builder()
                .id(UUID.randomUUID())
                .title("The Godfather")
                .build();
        entityManager.persist(m2);
        m3 = Movie.builder()
                .id(UUID.randomUUID())
                .title("The Dark Knight")
                .build();
        entityManager.persist(m3);
        m4 = Movie.builder()
                .id(UUID.randomUUID())
                .title("Pulp Fiction")
                .build();
        entityManager.persist(m4);
        entityManager.flush();

        var userIds = List.of(UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString());

        Map<String, Map<Movie, List<Integer>>> ratings = new HashMap<>();
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

        for (var userEntry : ratings.entrySet()) {
            var userId = userEntry.getKey();
            for (Map.Entry<Movie, List<Integer>> movieEntry : userEntry.getValue().entrySet()) {
                Movie movie = movieEntry.getKey();
                List<Integer> vals = movieEntry.getValue();

                UserMovieRating umr = UserMovieRating.builder()
                        .userId(userId)
                        .movie(movie)
                        .build();
                entityManager.persist(umr);

                RatingDetail rd1 = RatingDetail.builder()
                        .rating(vals.get(0))
                        .ratingCategory(c1)
                        .userMovieRating(umr)
                        .build();
                entityManager.persist(rd1);

                RatingDetail rd2 = RatingDetail.builder()
                        .rating(vals.get(1))
                        .ratingCategory(c2)
                        .userMovieRating(umr)
                        .build();
                entityManager.persist(rd2);

                RatingDetail rd3 = RatingDetail.builder()
                        .rating(vals.get(2))
                        .ratingCategory(c3)
                        .userMovieRating(umr)
                        .build();
                entityManager.persist(rd3);

                entityManager.flush();
            }
        }
    }

    @Test
    void findRecommendationsByCategoriesAboveThreshold_AllCategoriesAboveThreshold80_ReturnsAboveThreshold() {
        Set<UUID> categories = Set.of(c1.getId(), c2.getId(), c3.getId());
        var result = movieRepository.findRecommendationsByCategoriesAboveThreshold(categories, 80, PageRequest.of(0, 10));
        assertEquals(2, result.size());
        assertEquals(m3.getId(), result.get(0).getId());
        assertEquals(m4.getId(), result.get(1).getId());
    }

    @Test
    void findRecommendationsByCategoriesAboveThreshold_Threshold0Pagination_ReturnsAboveThreshold() {
        Set<UUID> categories = Set.of(c1.getId(), c2.getId(), c3.getId());
        var result = movieRepository.findRecommendationsByCategoriesAboveThreshold(categories, 0, PageRequest.of(0, 2));
        assertEquals(2, result.size());
        assertEquals(m3.getId(), result.get(0).getId());
        assertEquals(m4.getId(), result.get(1).getId());
    }

    @Test
    void findRecommendationsByCategoriesAboveThreshold_EmptyCategories_ReturnsNothing() {
        var result = movieRepository.findRecommendationsByCategoriesAboveThreshold(Set.of(), 0, PageRequest.of(0, 10));
        assertTrue(result.isEmpty());
    }

    @Test
    void findSimilarMovies_ForMovie1_ReturnsSimilarMovies() {
        var result = movieRepository.findSimilarMovies(m1.getId(), PageRequest.of(0, 10));
        assertEquals(3, result.size());
        assertEquals(m3.getId(), result.get(0).getId());
        assertEquals(m4.getId(), result.get(1).getId());
        assertEquals(m2.getId(), result.get(2).getId());
    }

    @Test
    void findSimilarMovies_Pagination_ReturnsSimilarMovies() {
        var result = movieRepository.findSimilarMovies(m1.getId(), PageRequest.of(0, 2));
        assertEquals(2, result.size());
        assertEquals(m3.getId(), result.get(0).getId());
        assertEquals(m4.getId(), result.get(1).getId());
    }

    @Test
    void findAverageMovieRatingsByMovieIdIn_ForMovies1And2_ReturnsSimilarMovies() {
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