package org.movierc.recommendationservice.repository;

import org.movierc.recommendationservice.entity.UserMovieRating;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserMovieRatingRepository {
    private final List<UserMovieRating> userMovieRatings = new ArrayList<>();

    public List<UserMovieRating> findAll() {
        return new ArrayList<>(userMovieRatings);
    }

    public List<UserMovieRating> findByUserId(UUID userId) {
        return userMovieRatings.stream()
                .filter(r -> r.getUserId().equals(userId))
                .toList();
    }

    public List<UserMovieRating> findByMovieId(UUID movieId) {
        return userMovieRatings.stream()
                .filter(r -> r.getMovieId().equals(movieId))
                .toList();
    }

    public Optional<UserMovieRating> findByUserIdAndMovieId(UUID userId, UUID movieId) {
        return userMovieRatings.stream()
                .filter(r -> r.getUserId().equals(userId) && r.getMovieId().equals(movieId))
                .findFirst();
    }

    public boolean existsByUserIdAndMovieId(UUID userId, UUID movieId) {
        return userMovieRatings.stream()
                .anyMatch(r -> Objects.equals(r.getUserId(), userId) && Objects.equals(r.getMovieId(), movieId));
    }

    public void deleteByUserIdAndMovieId(UUID userId, UUID movieId) {
        userMovieRatings.removeIf(r -> Objects.equals(r.getUserId(), userId) && Objects.equals(r.getMovieId(), movieId));
    }

    public UserMovieRating save(UserMovieRating userMovieRating) {
        if (userMovieRatings.stream().anyMatch(r -> r.getUserId().equals(userMovieRating.getUserId()) && r.getMovieId().equals(userMovieRating.getMovieId()))) {
            userMovieRatings.removeIf(r -> r.getUserId().equals(userMovieRating.getUserId()) && r.getMovieId().equals(userMovieRating.getMovieId()));
        }

        userMovieRatings.add(userMovieRating);
        return userMovieRating;
    }


}
