package org.movierc.feedbackservice.repository;

import org.movierc.feedbackservice.entity.UserMovieRating;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserMovieRatingRepository {
    private final List<UserMovieRating> userMovieRatings = new ArrayList<>();

    public List<UserMovieRating> findAll() {
        return new ArrayList<>(userMovieRatings);
    }

    public List<UserMovieRating> findByUserId(UUID userId) {
        return userMovieRatings.stream().filter(umr -> umr.getUser().getId().equals(userId)).toList();
    }

    public List<UserMovieRating> findByMovieId(UUID movieId) {
        return userMovieRatings.stream().filter(umr -> umr.getMovie().getId().equals(movieId)).toList();
    }

    public Optional<UserMovieRating> findById(UUID id) {
        return userMovieRatings.stream().filter(umr -> umr.getId().equals(id)).findFirst();
    }

    public boolean existsById(UUID id) {
        return userMovieRatings.stream().anyMatch(umr -> umr.getId().equals(id));
    }

    public void deleteById(UUID id) {
        userMovieRatings.removeIf(umr -> umr.getId().equals(id));
    }

    public UserMovieRating save(UserMovieRating userMovieRating) {
        if (userMovieRatings.stream().anyMatch(umr -> umr.getId().equals(userMovieRating.getId()))) {
            userMovieRatings.removeIf(umr -> umr.getId().equals(userMovieRating.getId()));
        }

        userMovieRatings.add(userMovieRating);
        return userMovieRating;
    }
}
