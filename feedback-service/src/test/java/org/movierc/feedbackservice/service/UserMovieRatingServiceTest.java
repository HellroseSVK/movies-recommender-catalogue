package org.movierc.feedbackservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.entity.Movie;
import org.movierc.feedbackservice.entity.User;
import org.movierc.feedbackservice.entity.UserMovieRating;
import org.movierc.feedbackservice.exception.UserMovieRatingNotFoundException;
import org.movierc.feedbackservice.exception.UserNotFoundException;
import org.movierc.feedbackservice.mapper.MovieRatingMapper;
import org.movierc.feedbackservice.mapper.RatingDetailDtoMapper;
import org.movierc.feedbackservice.mapper.UserMovieRatingCreateDtoMapper;
import org.movierc.feedbackservice.rabbit.UserMovieRatingEventProducer;
import org.movierc.feedbackservice.repository.MovieRepository;
import org.movierc.feedbackservice.repository.UserMovieRatingRepository;
import org.movierc.feedbackservice.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserMovieRatingServiceTest {

    @Mock private UserMovieRatingRepository userMovieRatingRepository;
    @Mock private UserRepository userRepository;
    @Mock private MovieRepository movieRepository;
    @Mock private MovieRatingMapper movieRatingMapper;
    @Mock private UserMovieRatingCreateDtoMapper userMovieRatingCreateDtoMapper;

    @Mock private RatingDetailDtoMapper ratingDetailDtoMapper;
    @Mock private UserMovieRatingEventProducer userMovieRatingEventProducer;

    private UserMovieRatingService userMovieRatingService;

    @BeforeEach
    void setUp() {
        userMovieRatingService = new UserMovieRatingService(
            userMovieRatingRepository,
            userRepository,
            movieRepository,
            movieRatingMapper,
            userMovieRatingCreateDtoMapper,
            ratingDetailDtoMapper,
            userMovieRatingEventProducer
        );
    }

    @Test
    void getAllRatings_ReturnsRatingList() {
        UserMovieRating entity = new UserMovieRating();
        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingRepository.findAll()).thenReturn(List.of(entity));
        when(movieRatingMapper.userMovieRatingTouserMovieRatingDto(entity)).thenReturn(dto);

        List<UserMovieRatingDto> result = userMovieRatingService.getAllRatings();

        assertEquals(1, result.size());
        verify(userMovieRatingRepository).findAll();
    }

    @Test
    void getRatingsByUserId_ReturnsRatingList() {
        UUID userId = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingRepository.findByUserId(userId)).thenReturn(List.of(entity));
        when(movieRatingMapper.userMovieRatingTouserMovieRatingDto(entity)).thenReturn(dto);

        List<UserMovieRatingDto> result = userMovieRatingService.getRatingsByUserId(userId);

        assertEquals(1, result.size());
        verify(userMovieRatingRepository).findByUserId(userId);
    }

    @Test
    void getRatingsByMovieId_ReturnsRatingList() {
        UUID movieId = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingRepository.findByMovieId(movieId)).thenReturn(List.of(entity));
        when(movieRatingMapper.userMovieRatingTouserMovieRatingDto(entity)).thenReturn(dto);

        List<UserMovieRatingDto> result = userMovieRatingService.getRatingsByMovieId(movieId);

        assertEquals(1, result.size());
        verify(userMovieRatingRepository).findByMovieId(movieId);
    }

    @Test
    void getRatingById_WhenExists_ReturnsDto() {
        UUID id = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();
        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.of(entity));
        when(movieRatingMapper.userMovieRatingTouserMovieRatingDto(entity)).thenReturn(dto);

        UserMovieRatingDto result = userMovieRatingService.getRatingById(id);

        assertEquals(dto, result);
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
    void createRating_ValidInput_ReturnsDto() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();

        UserMovieRatingCreateDto createDto = UserMovieRatingCreateDto.builder()
            .userId(userId)
            .movieId(movieId)
            .ratingDetails(Collections.emptyList())
            .comment("Great movie!")
            .build();

        UserMovieRating entity = new UserMovieRating();
        UserMovieRatingDto dto = new UserMovieRatingDto();
        User user = new User();
        Movie movie = new Movie();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(userMovieRatingCreateDtoMapper.userMovieRatingCreateDtoToUserMovieRating(createDto)).thenReturn(entity);
        when(userMovieRatingRepository.save(entity)).thenReturn(entity);
        when(movieRatingMapper.userMovieRatingTouserMovieRatingDto(entity)).thenReturn(dto);

        UserMovieRatingDto result = userMovieRatingService.createRating(createDto);

        assertEquals(dto, result);
        verify(userMovieRatingRepository).save(entity);
        verify(userMovieRatingEventProducer).sendMovieRatingCreatedEvent(any());
    }

    @Test
    void createRating_UserNotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();

        UserMovieRatingCreateDto createDto = UserMovieRatingCreateDto.builder()
            .userId(userId)
            .movieId(UUID.randomUUID())
            .ratingDetails(Collections.emptyList())
            .comment("Nice.")
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userMovieRatingService.createRating(createDto));
        verify(userRepository).findById(userId);
    }

    @Test
    void updateRating_WhenExists_ReturnsUpdatedDto() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();

        UserMovieRatingUpdateDto updateDto = UserMovieRatingUpdateDto.builder()
            .userId(userId)
            .movieId(movieId)
            .ratingDetails(Collections.emptyList())
            .comment("Updated comment")
            .build();

        UserMovieRating entity = new UserMovieRating();
        UserMovieRatingDto dto = new UserMovieRatingDto();
        User user = new User();
        Movie movie = new Movie();

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.of(entity));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(userMovieRatingRepository.save(entity)).thenReturn(entity);
        when(movieRatingMapper.userMovieRatingTouserMovieRatingDto(entity)).thenReturn(dto);

        UserMovieRatingDto result = userMovieRatingService.updateRating(id, updateDto);

        assertEquals(dto, result);
        verify(userMovieRatingRepository).save(entity);
        verify(userMovieRatingEventProducer).sendMovieRatingUpdatedEvent(any());
    }

    @Test
    void updateRating_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();

        UserMovieRatingUpdateDto updateDto = UserMovieRatingUpdateDto.builder()
            .userId(UUID.randomUUID())
            .movieId(UUID.randomUUID())
            .ratingDetails(Collections.emptyList())
            .comment("Nothing to update")
            .build();

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserMovieRatingNotFoundException.class, () -> userMovieRatingService.updateRating(id, updateDto));
        verify(userMovieRatingRepository).findById(id);
    }

    @Test
    void deleteRating_WhenExists_DeletesSuccessfully() {
        UUID id = UUID.randomUUID();
        UserMovieRating entity = new UserMovieRating();

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.of(entity));

        userMovieRatingService.deleteRating(id);

        verify(userMovieRatingRepository).deleteById(id);
        verify(userMovieRatingEventProducer).sendMovieRatingDeletedEvent(any());
    }

    @Test
    void deleteRating_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();

        when(userMovieRatingRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(UserMovieRatingNotFoundException.class, () -> userMovieRatingService.deleteRating(id));
        verify(userMovieRatingRepository).findById(id);
    }
}
