package org.movierc.recommendationservice.repository;

import org.movierc.recommendationservice.entity.AverageMovieRating;
import org.movierc.recommendationservice.entity.Movie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, UUID> {
    /**
     * Finds movies that have an average rating above a certain threshold for a given set of rating categories.
     *
     * @param categoryIds categories for which the average rating is calculated
     * @param threshold minimum average rating threshold
     * @param pageable pagination information
     * @return a list of movies that meet the criteria
     */
    @Query(
            value = "SELECT m " +
                    "FROM Movie m " +
                    "INNER JOIN UserMovieRating umr ON m = umr.movie " +
                    "INNER JOIN RatingDetail rd ON umr = rd.userMovieRating " +
                    "WHERE rd.ratingCategory.id IN :categoryIds " +
                    "GROUP BY m.id " +
                    "HAVING AVG(rd.rating) > :threshold " +
                    "ORDER BY AVG(rd.rating) DESC"
    )
    List<Movie> findRecommendationsByCategoriesAboveThreshold(Set<UUID> categoryIds, int threshold, Pageable pageable);

    /**
     * Finds movies that are similar to a given movie based on user ratings.
     *
     * @param movieId movie ID to find similar movies for
     * @param pageable pagination information
     * @return a list of similar movies
     */
    @Query(
            value = "SELECT ot_m, AVG(rd.rating) as rating " +
                    "FROM Movie my_m " +
                    "INNER JOIN UserMovieRating my_umr ON my_m = my_umr.movie " +
                    "INNER JOIN UserMovieRating ot_umr ON my_umr.userId = ot_umr.userId " +
                    "INNER JOIN Movie ot_m ON ot_umr.movie = ot_m " +
                    "INNER JOIN RatingDetail rd ON rd.userMovieRating = ot_umr " +
                    "WHERE  my_m.id = :movieId AND ot_m != my_m " +
                    "GROUP BY ot_m.id " +
                    "ORDER BY rating DESC"
    )
    List<Movie> findSimilarMovies(UUID movieId, Pageable pageable);

    /**
     * Finds the average movie ratings for rated categories for a set of movies.
     *
     * @param movieIds the IDs of the movies to find average ratings for
     * @param pageable pagination information
     * @return a list of average movie ratings
     */
    @Query(
            "SELECT NEW org.movierc.recommendationservice.entity.AverageMovieRating(m.id, rd.ratingCategory.id, rd.ratingCategory.name, AVG(rd.rating))" +
                    " FROM Movie m" +
                    " INNER JOIN UserMovieRating umr ON m = umr.movie" +
                    " INNER JOIN RatingDetail rd ON umr = rd.userMovieRating" +
                    " WHERE m.id IN :movieIds" +
                    " GROUP BY m.id, rd.ratingCategory.id"
    )
    List<AverageMovieRating> findAverageMovieRatingsByMovieIdIn(Set<UUID> movieIds, Pageable pageable);
}
