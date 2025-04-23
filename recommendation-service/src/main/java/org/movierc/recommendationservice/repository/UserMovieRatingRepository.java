package org.movierc.recommendationservice.repository;

import org.movierc.recommendationservice.entity.UserMovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMovieRatingRepository extends JpaRepository<UserMovieRating, UUID> {
    List<UserMovieRating> findByUserId(UUID userId);

    List<UserMovieRating> findByMovieId(UUID movieId);

    Optional<UserMovieRating> findByUserIdAndMovieId(UUID userId, UUID movieId);

    boolean existsByUserIdAndMovieId(UUID userId, UUID movieId);

    void deleteByUserIdAndMovieId(UUID userId, UUID movieId);
}
