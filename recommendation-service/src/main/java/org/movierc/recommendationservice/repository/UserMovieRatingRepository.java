package org.movierc.recommendationservice.repository;

import org.movierc.recommendationservice.entity.UserMovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMovieRatingRepository extends JpaRepository<UserMovieRating, UUID> {
    Optional<UserMovieRating> findByUserIdAndMovieId(String userId, UUID movieId);

    boolean existsByUserIdAndMovieId(String userId, UUID movieId);

    void deleteByUserIdAndMovieId(String userId, UUID movieId);
}
