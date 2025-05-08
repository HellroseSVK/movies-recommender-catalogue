package org.movierc.feedbackservice.service;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.entity.RatingDetail;
import org.movierc.feedbackservice.entity.UserMovieRating;
import org.movierc.feedbackservice.exception.MovieNotFoundException;
import org.movierc.feedbackservice.exception.RatingCategoryNotFoundException;
import org.movierc.feedbackservice.exception.UserMovieRatingNotFoundException;
import org.movierc.feedbackservice.repository.MovieRepository;
import org.movierc.feedbackservice.repository.RatingCategoryRepository;
import org.movierc.feedbackservice.repository.UserMovieRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class UserMovieRatingService {
    private final UserMovieRatingRepository userMovieRatingRepository;
    private final MovieRepository movieRepository;
    private final RatingCategoryRepository ratingCategoryRepository;

    @Transactional(readOnly = true)
    public List<UserMovieRating> getAllRatings() {
        return userMovieRatingRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UserMovieRating> getRatingsByUserId(String userId) {
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

    public UserMovieRating createRating(UserMovieRating userMovieRating, UUID movieId) {
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

    public UserMovieRating updateRating(
            UUID id,
            List<RatingDetail> ratingDetails,
            String comment,
            String requiredUserId
    ) {
        var userMovieRating = userMovieRatingRepository.findById(id)
                .orElseThrow(() -> new UserMovieRatingNotFoundException(id));

        if (requiredUserId != null && !userMovieRating.getUserId().equals(requiredUserId)) {
            throw new UserMovieRatingNotFoundException(id);
        }

        userMovieRating.setRatingDetails(ratingDetails);
        userMovieRating.setComment(comment);

        return userMovieRatingRepository.save(userMovieRating);
    }

    public UserMovieRating deleteRating(UUID id, String requiredUserId) {
        var userMovieRating = userMovieRatingRepository.findById(id)
                .orElseThrow(() -> new UserMovieRatingNotFoundException(id));

        if (requiredUserId != null && !userMovieRating.getUserId().equals(requiredUserId)) {
            throw new UserMovieRatingNotFoundException(id);
        }

        userMovieRatingRepository.deleteById(id);

        return userMovieRating;
    }
}
