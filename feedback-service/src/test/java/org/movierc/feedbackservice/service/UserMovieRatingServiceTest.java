package org.movierc.feedbackservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.feedbackservice.entity.Movie;
import org.movierc.feedbackservice.entity.RatingDetail;
import org.movierc.feedbackservice.entity.UserMovieRating;
import org.movierc.feedbackservice.exception.UserMovieRatingNotFoundException;
import org.movierc.feedbackservice.repository.MovieRepository;
import org.movierc.feedbackservice.repository.UserMovieRatingRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMovieRatingServiceTest {

    @Mock private UserMovieRatingRepository userMovieRatingRepository;
    @Mock private MovieRepository movieRepository;

    @InjectMocks
    private UserMovieRatingService userMovieRatingService;

    @Test
    void getAllRatings_ReturnsRatingList() {
        UserMovieRating entity = new UserMovieRating();
        when(userMovieRatingRepository.findAll()).thenReturn(List.of(entity));

        List<UserMovieRating> result = userMovieRatingService.getAllRatings();

        assertEquals(1, result.size());
        verify(userMovieRatingRepository).findAll();
    }

    @Test
    void getRatingsByUserId_ReturnsRatingList() {
        var userId = UUID.randomUUID().toString();
        UserMovieRating entity = new UserMovieRating();
        when(userMovieRatingRepository.findByUserId(userId)).thenReturn(List.of(entity));

        List<UserMovieRating> result = userMovieRatingService.getRatingsByUserId(userId);

        assertEquals(1, result.size());
        verify(userMovieRatingRepository).findByUserId(userId);
    }

    @Test
    void getRatingsByMovieId_ReturnsRatingList() {
        UUID movieId = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        when(movieRepository.existsById(movieId)).thenReturn(true);
        when(userMovieRatingRepository.findByMovieId(movieId)).thenReturn(List.of(entity));

        List<UserMovieRating> result = userMovieRatingService.getRatingsByMovieId(movieId);

        assertEquals(1, result.size());
        verify(userMovieRatingRepository).findByMovieId(movieId);
    }

    @Test
    void getRatingById_WhenExists_ReturnsEntity() {
        UUID id = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.of(entity));

        UserMovieRating result = userMovieRatingService.getRatingById(id);

        assertEquals(entity, result);
        verify(userMovieRatingRepository).findById(id);
    }

    @Test
    void getRatingById_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserMovieRatingNotFoundException.class, () -> userMovieRatingService.getRatingById(id));
        verify(userMovieRatingRepository).findById(id);
    }

    @Test
    void createRating_ValidInput_ReturnsEntity() {
        UUID movieId = UUID.randomUUID();
        Movie movie = new Movie();
        UserMovieRating entity = new UserMovieRating();
        entity.setRatingDetails(Collections.emptyList());

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(userMovieRatingRepository.save(entity)).thenReturn(entity);

        UserMovieRating result = userMovieRatingService.createRating(entity, movieId);

        assertEquals(entity, result);
        verify(userMovieRatingRepository).save(entity);
    }

    @Test
    void updateRating_WhenExists_ReturnsUpdatedEntity() {
        UUID id = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        List<RatingDetail> ratingDetails = Collections.emptyList();
        String comment = "Updated comment";

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userMovieRatingRepository.save(entity)).thenReturn(entity);

        UserMovieRating result = userMovieRatingService.updateRating(id, ratingDetails, comment, null);

        assertEquals(entity, result);
        verify(userMovieRatingRepository).save(entity);
    }

    @Test
    void updateRating_WhenExistsButWrongUser_ThrowsException() {
        UUID id = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        entity.setUserId("user1");
        List<RatingDetail> ratingDetails = Collections.emptyList();

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(UserMovieRatingNotFoundException.class, () -> userMovieRatingService.updateRating(id, ratingDetails, "No update", "user2"));
    }

    @Test
    void updateRating_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        List<RatingDetail> ratingDetails = Collections.emptyList();

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserMovieRatingNotFoundException.class, () -> userMovieRatingService.updateRating(id, ratingDetails, "No update", null));
        verify(userMovieRatingRepository).findById(id);
    }

    @Test
    void deleteRating_WhenExists_DeletesSuccessfully() {
        UUID id = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.of(entity));

        UserMovieRating result = userMovieRatingService.deleteRating(id, null);

        assertEquals(entity, result);
        verify(userMovieRatingRepository).deleteById(id);
    }

    @Test
    void deleteRating_WhenExistsButWrongUser_ThrowsException() {
        UUID id = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        entity.setUserId("user1");

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.of(entity));

        assertThrows(UserMovieRatingNotFoundException.class, () -> userMovieRatingService.deleteRating(id, "user2"));
    }


    @Test
    void deleteRating_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserMovieRatingNotFoundException.class, () -> userMovieRatingService.deleteRating(id, null));
        verify(userMovieRatingRepository).findById(id);
    }
}
