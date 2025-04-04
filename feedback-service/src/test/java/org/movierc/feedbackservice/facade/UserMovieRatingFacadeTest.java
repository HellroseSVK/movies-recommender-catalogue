package org.movierc.feedbackservice.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.service.UserMovieRatingService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMovieRatingFacadeTest {

    @Mock
    private UserMovieRatingService userMovieRatingService;

    private UserMovieRatingFacade userMovieRatingFacade;

    @BeforeEach
    void setUp() {
        userMovieRatingFacade = new UserMovieRatingFacade(userMovieRatingService);
    }

    @Test
    void getAllRatings_ReturnsList() {
        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingService.getAllRatings()).thenReturn(List.of(dto));

        List<UserMovieRatingDto> result = userMovieRatingFacade.getAllRatings();

        assertEquals(1, result.size());
        verify(userMovieRatingService).getAllRatings();
    }

    @Test
    void getRatingsByUserId_ReturnsList() {
        UUID userId = UUID.randomUUID();
        when(userMovieRatingService.getRatingsByUserId(userId)).thenReturn(Collections.singletonList(new UserMovieRatingDto()));

        List<UserMovieRatingDto> result = userMovieRatingFacade.getRatingsByUserId(userId);

        assertEquals(1, result.size());
        verify(userMovieRatingService).getRatingsByUserId(userId);
    }

    @Test
    void getRatingsByMovieId_ReturnsList() {
        UUID movieId = UUID.randomUUID();
        when(userMovieRatingService.getRatingsByMovieId(movieId)).thenReturn(Collections.singletonList(new UserMovieRatingDto()));

        List<UserMovieRatingDto> result = userMovieRatingFacade.getRatingsByMovieId(movieId);

        assertEquals(1, result.size());
        verify(userMovieRatingService).getRatingsByMovieId(movieId);
    }

    @Test
    void getRatingById_ReturnsDto() {
        UUID ratingId = UUID.randomUUID();
        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingService.getRatingById(ratingId)).thenReturn(dto);

        UserMovieRatingDto result = userMovieRatingFacade.getRatingById(ratingId);

        assertNotNull(result);
        verify(userMovieRatingService).getRatingById(ratingId);
    }

    @Test
    void createRating_ReturnsCreatedDto() {
        UserMovieRatingCreateDto createDto = UserMovieRatingCreateDto.builder()
            .userId(UUID.randomUUID())
            .movieId(UUID.randomUUID())
            .ratingDetails(Collections.emptyList())
            .comment("Great movie!")
            .build();

        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingService.createRating(createDto)).thenReturn(dto);

        UserMovieRatingDto result = userMovieRatingFacade.createRating(createDto);

        assertNotNull(result);
        verify(userMovieRatingService).createRating(createDto);
    }

    @Test
    void updateRating_ReturnsUpdatedDto() {
        UUID ratingId = UUID.randomUUID();
        UserMovieRatingUpdateDto updateDto = UserMovieRatingUpdateDto.builder()
            .userId(UUID.randomUUID())
            .movieId(UUID.randomUUID())
            .ratingDetails(Collections.emptyList())
            .comment("Updated comment")
            .build();

        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingService.updateRating(ratingId, updateDto)).thenReturn(dto);

        UserMovieRatingDto result = userMovieRatingFacade.updateRating(ratingId, updateDto);

        assertNotNull(result);
        verify(userMovieRatingService).updateRating(ratingId, updateDto);
    }

    @Test
    void deleteRating_CallsService() {
        UUID ratingId = UUID.randomUUID();

        userMovieRatingFacade.deleteRating(ratingId);

        verify(userMovieRatingService).deleteRating(ratingId);
    }
}
