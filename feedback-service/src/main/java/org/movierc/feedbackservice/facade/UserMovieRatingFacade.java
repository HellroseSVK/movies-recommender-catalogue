package org.movierc.feedbackservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.service.UserMovieRatingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserMovieRatingFacade {
    private final UserMovieRatingService userMovieRatingService;

    public List<UserMovieRatingDto> getAllRatings() {
        return userMovieRatingService.getAllRatings();
    }

    public List<UserMovieRatingDto> getRatingsByUserId(UUID userId) {
        return userMovieRatingService.getRatingsByUserId(userId);
    }

    public List<UserMovieRatingDto> getRatingsByMovieId(UUID movieId) {
        return userMovieRatingService.getRatingsByMovieId(movieId);
    }

    public UserMovieRatingDto getRatingById(UUID id) {
        return userMovieRatingService.getRatingById(id);
    }

    public UserMovieRatingDto createRating(UserMovieRatingCreateDto userMovieRatingCreateDto) {
        return userMovieRatingService.createRating(userMovieRatingCreateDto);
    }

    public UserMovieRatingDto updateRating(UUID id, UserMovieRatingUpdateDto userMovieRatingUpdateDto) {
        return userMovieRatingService.updateRating(id, userMovieRatingUpdateDto);
    }

    public void deleteRating(UUID id) {
        userMovieRatingService.deleteRating(id);
    }
}
