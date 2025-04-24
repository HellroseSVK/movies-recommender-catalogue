package org.movierc.feedbackservice.repository;

import org.movierc.feedbackservice.entity.UserMovieRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserMovieRatingRepository extends JpaRepository<UserMovieRating, UUID> {
    List<UserMovieRating> findByUserId(UUID userId);
    List<UserMovieRating> findByMovieId(UUID movieId);
}
