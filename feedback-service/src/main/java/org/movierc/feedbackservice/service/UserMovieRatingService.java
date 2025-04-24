package org.movierc.feedbackservice.service;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.entity.RatingDetail;
import org.movierc.feedbackservice.entity.UserMovieRating;
import org.movierc.feedbackservice.exception.MovieNotFoundException;
import org.movierc.feedbackservice.exception.RatingCategoryNotFoundException;
import org.movierc.feedbackservice.exception.UserMovieRatingNotFoundException;
import org.movierc.feedbackservice.exception.UserNotFoundException;
import org.movierc.feedbackservice.repository.MovieRepository;
import org.movierc.feedbackservice.repository.RatingCategoryRepository;
import org.movierc.feedbackservice.repository.UserMovieRatingRepository;
import org.movierc.feedbackservice.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserMovieRatingService {
    private final UserMovieRatingRepository userMovieRatingRepository;
    private final UserRepository userRepository;
    private final MovieRepository movieRepository;
    private final RatingCategoryRepository ratingCategoryRepository;

    @Transactional(readOnly = true)
    public List<UserMovieRating> getAllRatings() {
        return userMovieRatingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserMovieRating> getRatingsByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        return userMovieRatingRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<UserMovieRating> getRatingsByMovieId(UUID movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException(movieId);
        }

        return userMovieRatingRepository.findByMovieId(movieId);
    }

    @Transactional(readOnly = true)
    public UserMovieRating getRatingById(UUID id) {
        return userMovieRatingRepository.findById(id)
                .orElseThrow(() -> new UserMovieRatingNotFoundException(id));
    }

    public UserMovieRating createRating(UserMovieRating userMovieRating, UUID userId, UUID movieId) {
        userMovieRating.setUser(
                userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId))
        );
        userMovieRating.setMovie(
                movieRepository.findById(movieId)
                        .orElseThrow(() -> new MovieNotFoundException(movieId))
        );
        userMovieRating.getRatingDetails().forEach(
                ratingDetail -> ratingCategoryRepository.findById(ratingDetail.getRatingCategoryId())
                        .orElseThrow(() -> new RatingCategoryNotFoundException(ratingDetail.getRatingCategoryId()))
        );

        return userMovieRatingRepository.save(userMovieRating);
    }

    public UserMovieRating updateRating(UUID id, UUID userId, UUID movieId, List<RatingDetail> ratingDetails, String comment) {
        var userMovieRating = userMovieRatingRepository.findById(id)
                .orElseThrow(() -> new UserMovieRatingNotFoundException(id));

        userMovieRating.setUser(
                userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(userId))
        );
        userMovieRating.setMovie(
                movieRepository.findById(movieId)
                        .orElseThrow(() -> new UserMovieRatingNotFoundException(movieId))
        );
        userMovieRating.setRatingDetails(ratingDetails);
        userMovieRating.setComment(comment);


        return userMovieRatingRepository.save(userMovieRating);
    }

    public UserMovieRating deleteRating(UUID id) {
        var userMovieRating = userMovieRatingRepository.findById(id)
                .orElseThrow(() -> new UserMovieRatingNotFoundException(id));

        userMovieRatingRepository.deleteById(id);

        return userMovieRating;
    }
}
