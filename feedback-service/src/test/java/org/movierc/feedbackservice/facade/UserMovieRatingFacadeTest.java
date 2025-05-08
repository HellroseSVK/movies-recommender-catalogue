package org.movierc.feedbackservice.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.feedbackservice.artemis.UserMovieRatingEventArtemisProducer;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.entity.UserMovieRating;
import org.movierc.feedbackservice.mapper.MovieMapper;
import org.movierc.feedbackservice.mapper.MovieRatingMapper;
import org.movierc.feedbackservice.mapper.RatingDetailMapper;
import org.movierc.feedbackservice.mapper.UserMovieRatingCreateDtoMapper;
import org.movierc.feedbackservice.rabbit.UserMovieRatingEventProducer;
import org.movierc.feedbackservice.service.UserMovieRatingService;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMovieRatingFacadeTest {

    @Mock private UserMovieRatingService userMovieRatingService;
    @Mock private UserMovieRatingEventProducer userMovieRatingEventProducer;
    @Mock private UserMovieRatingEventArtemisProducer userMovieRatingEventArtemisProducer;

    private UserMovieRatingFacade userMovieRatingFacade;

    private final MovieRatingMapper movieRatingMapper = Mappers.getMapper(MovieRatingMapper.class);
    private final RatingDetailMapper ratingDetailMapper = Mappers.getMapper(RatingDetailMapper.class);
    private final UserMovieRatingCreateDtoMapper userMovieRatingCreateDtoMapper = Mappers.getMapper(UserMovieRatingCreateDtoMapper.class);
    private final MovieMapper movieMapper = Mappers.getMapper(MovieMapper.class);

    @BeforeEach
    void setUp() throws Exception {
        userMovieRatingFacade = new UserMovieRatingFacade(
            userMovieRatingService,
            movieRatingMapper,
            userMovieRatingCreateDtoMapper,
            ratingDetailMapper,
            userMovieRatingEventProducer,
            userMovieRatingEventArtemisProducer
        );

      var field = movieRatingMapper.getClass().getDeclaredField("movieMapper");
      field.setAccessible(true);
      field.set(movieRatingMapper, movieMapper);
      field.setAccessible(false);
    }

    @Test
    void getAllRatings_ReturnsList() {
        UserMovieRating entity = new UserMovieRating();
        entity.setComment("Great movie!");
        when(userMovieRatingService.getAllRatings()).thenReturn(List.of(entity));

        List<UserMovieRatingDto> result = userMovieRatingFacade.getAllRatings();

        assertEquals(1, result.size());
        assertEquals("Great movie!", result.get(0).getComment());
        verify(userMovieRatingService).getAllRatings();
    }

    @Test
    void getRatingsByUserId_ReturnsList() {
        var userId = UUID.randomUUID().toString();
        UserMovieRating entity = new UserMovieRating();
        entity.setComment("User-specific rating");
        when(userMovieRatingService.getRatingsByUserId(userId)).thenReturn(List.of(entity));

        List<UserMovieRatingDto> result = userMovieRatingFacade.getRatingsByUserId(userId);

        assertEquals(1, result.size());
        assertEquals("User-specific rating", result.get(0).getComment());
        verify(userMovieRatingService).getRatingsByUserId(userId);
    }

    @Test
    void getRatingsByMovieId_ReturnsList() {
        UUID movieId = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        entity.setComment("Movie-specific rating");
        when(userMovieRatingService.getRatingsByMovieId(movieId)).thenReturn(List.of(entity));

        List<UserMovieRatingDto> result = userMovieRatingFacade.getRatingsByMovieId(movieId);

        assertEquals(1, result.size());
        assertEquals("Movie-specific rating", result.get(0).getComment());
        verify(userMovieRatingService).getRatingsByMovieId(movieId);
    }

    @Test
    void getRatingById_ReturnsDto() {
        UUID ratingId = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        entity.setComment("Specific rating");
        when(userMovieRatingService.getRatingById(ratingId)).thenReturn(entity);

        UserMovieRatingDto result = userMovieRatingFacade.getRatingById(ratingId);

        assertNotNull(result);
        assertEquals("Specific rating", result.getComment());
        verify(userMovieRatingService).getRatingById(ratingId);
    }

    @Test
    void createRating_ReturnsMappedDto() {
        var userId = UUID.randomUUID().toString();
        UUID movieId = UUID.randomUUID();
        UserMovieRatingCreateDto createDto = UserMovieRatingCreateDto.builder()
            .userId(userId)
            .movieId(movieId)
            .ratingDetails(Collections.emptyList())
            .comment("New rating")
            .build();

        UserMovieRating entity = new UserMovieRating();
        entity.setComment("New rating");
        when(userMovieRatingService.createRating(
                any(), any()
        )).thenReturn(entity);

        UserMovieRatingDto result = userMovieRatingFacade.createRating(createDto);

        assertNotNull(result);
        assertEquals("New rating", result.getComment());
        verify(userMovieRatingService).createRating(
            argThat(e -> e.getComment().equals("New rating")),
                eq(movieId)
        );
        verify(userMovieRatingEventProducer).sendMovieRatingCreatedEvent(any());
        verify(userMovieRatingEventArtemisProducer).sendMovieRatingChangedEvent(any());
    }

    @Test
    void updateRating_ReturnsMappedDto() {
        UUID ratingId = UUID.randomUUID();
        var userId = UUID.randomUUID().toString();
        UUID movieId = UUID.randomUUID();
        UserMovieRatingUpdateDto updateDto = UserMovieRatingUpdateDto.builder()
            .userId(userId)
            .movieId(movieId)
            .ratingDetails(Collections.emptyList())
            .comment("Updated rating")
            .build();

        UserMovieRating entity = new UserMovieRating();
        entity.setComment("Updated rating");
        when(userMovieRatingService.updateRating(
                eq(ratingId),
                eq(userId),
                eq(movieId),
                eq(Collections.emptyList()),
                eq("Updated rating")
        )).thenReturn(entity);

        UserMovieRatingDto result = userMovieRatingFacade.updateRating(ratingId, updateDto);

        assertNotNull(result);
        assertEquals("Updated rating", result.getComment());
        verify(userMovieRatingService).updateRating(
            eq(ratingId),
            eq(userId),
            eq(movieId),
            eq(Collections.emptyList()),
            eq("Updated rating")
        );
        verify(userMovieRatingEventProducer).sendMovieRatingUpdatedEvent(any());
        verify(userMovieRatingEventArtemisProducer).sendMovieRatingChangedEvent(any());
    }

    @Test
    void deleteRating_CallsServiceAndSendsEvent() {
        UUID ratingId = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        when(userMovieRatingService.deleteRating(ratingId)).thenReturn(entity);

        userMovieRatingFacade.deleteRating(ratingId);

        verify(userMovieRatingService).deleteRating(ratingId);
        verify(userMovieRatingEventProducer).sendMovieRatingDeletedEvent(any());
        verify(userMovieRatingEventArtemisProducer).sendMovieRatingChangedEvent(any());
    }
}
