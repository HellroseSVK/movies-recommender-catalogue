package org.movierc.recommendationservice.rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.rating.MovieRatingCreatedEvent;
import org.movierc.events.rating.MovieRatingDeletedEvent;
import org.movierc.events.rating.MovieRatingUpdatedEvent;
import org.movierc.events.rating.RatingDetail;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.movierc.recommendationservice.entity.UserMovieRating;
import org.movierc.recommendationservice.exception.CategoryNotFoundException;
import org.movierc.recommendationservice.exception.MovieNotFoundException;
import org.movierc.recommendationservice.mapper.UserMovieRatingMapper;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.movierc.recommendationservice.repository.UserMovieRatingRepository;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserMovieRatingEventConsumerTest {
    @Mock
    private UserMovieRatingRepository userMovieRatingRepository;

    @Mock
    private UserMovieRatingMapper userMovieRatingMapper;

    @Mock
    private RatingCategoryRepository ratingCategoryRepository;

    @Mock
    private MovieRepository movieRepository;

    private UserMovieRatingEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new UserMovieRatingEventConsumer(
            userMovieRatingRepository,
            userMovieRatingMapper,
            ratingCategoryRepository,
            movieRepository
        );
    }

    @Test
    void handleMovieRatingCreatedEvent_UserMovieRatingNotExists_SavesUserMovieRating() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        RatingDetail detailEvent = RatingDetail.builder()
            .ratingCategoryId(categoryId)
            .rating(5)
            .build();
        MovieRatingCreatedEvent event = MovieRatingCreatedEvent.builder()
            .userId(userId)
            .movieId(movieId)
            .ratingDetails(Collections.singletonList(detailEvent))
            .build();

        when(userMovieRatingRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        Movie movie = Movie.builder().id(movieId).title("Test").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        UserMovieRating userMovieRating = UserMovieRating.builder()
            .userId(userId)
            .movie(movie)
            .build();
        when(userMovieRatingMapper.userMovieRatingCreatedEventToUserMovieRating(event, movie))
            .thenReturn(userMovieRating);
        RatingCategory category = RatingCategory.builder().id(categoryId).name("Category").build();
        when(ratingCategoryRepository.findByIdIn(Set.of(categoryId)))
            .thenReturn(Collections.singletonList(category));

        consumer.handleMovieRatingCreatedEvent(event);

        ArgumentCaptor<UserMovieRating> captor = ArgumentCaptor.forClass(UserMovieRating.class);
        verify(userMovieRatingRepository, times(1)).save(captor.capture());
        UserMovieRating saved = captor.getValue();
        assertSame(userMovieRating, saved);
        assertNotNull(saved.getRatingDetails());
        assertEquals(1, saved.getRatingDetails().size());
        assertEquals(5, saved.getRatingDetails().get(0).getRating());
        assertSame(category, saved.getRatingDetails().get(0).getRatingCategory());
    }

    @Test
    void handleMovieRatingCreatedEvent_MovieNotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        MovieRatingCreatedEvent event = MovieRatingCreatedEvent.builder()
            .userId(userId)
            .movieId(movieId)
            .ratingDetails(Collections.emptyList())
            .build();
        when(userMovieRatingRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        assertThrows(MovieNotFoundException.class, () -> consumer.handleMovieRatingCreatedEvent(event));
    }

    @Test
    void handleMovieRatingCreatedEvent_UserMovieRatingExists_DoesNotSave() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        MovieRatingCreatedEvent event = MovieRatingCreatedEvent.builder()
            .userId(userId)
            .movieId(movieId)
            .ratingDetails(Collections.emptyList())
            .build();
        when(userMovieRatingRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(true);
        consumer.handleMovieRatingCreatedEvent(event);
        verify(userMovieRatingRepository, never()).save(any());
    }

    @Test
    void handleMovieRatingUpdatedEvent_UserMovieRatingExists_UpdatesUserMovieRating() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        RatingDetail detailEvent = RatingDetail.builder()
            .ratingCategoryId(categoryId)
            .rating(7)
            .build();
        MovieRatingUpdatedEvent event = MovieRatingUpdatedEvent.builder()
            .userId(userId)
            .movieId(movieId)
            .newRatingDetails(Collections.singletonList(detailEvent))
            .build();

        UserMovieRating existing = UserMovieRating.builder()
            .userId(userId)
            .movie(Movie.builder().id(movieId).title("Test").build())
            .ratingDetails(Collections.emptyList())
            .build();
        when(userMovieRatingRepository.findByUserIdAndMovieId(userId, movieId))
            .thenReturn(Optional.of(existing));
        RatingCategory category = RatingCategory.builder().id(categoryId).name("Category").build();
        when(ratingCategoryRepository.findByIdIn(Set.of(categoryId)))
            .thenReturn(Collections.singletonList(category));

        consumer.handleMovieRatingUpdatedEvent(event);

        verify(userMovieRatingRepository, times(1)).save(existing);
        assertEquals(1, existing.getRatingDetails().size());
        assertEquals(7, existing.getRatingDetails().get(0).getRating());
        assertSame(category, existing.getRatingDetails().get(0).getRatingCategory());
    }

    @Test
    void handleMovieRatingUpdatedEvent_UserMovieRatingNotExists_DoesNotSave() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        MovieRatingUpdatedEvent event = MovieRatingUpdatedEvent.builder()
            .userId(userId)
            .movieId(movieId)
            .newRatingDetails(Collections.emptyList())
            .build();
        when(userMovieRatingRepository.findByUserIdAndMovieId(userId, movieId))
            .thenReturn(Optional.empty());
        consumer.handleMovieRatingUpdatedEvent(event);
        verify(userMovieRatingRepository, never()).save(any());
    }

    @Test
    void handleMovieRatingDeletedEvent_UserMovieRatingNotExists_DeletesUserMovieRating() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        MovieRatingDeletedEvent event = MovieRatingDeletedEvent.builder()
            .userId(userId)
            .movieId(movieId)
            .build();
        when(userMovieRatingRepository.existsByUserIdAndMovieId(userId, movieId))
            .thenReturn(false);
        consumer.handleMovieRatingDeletedEvent(event);
        verify(userMovieRatingRepository, times(1)).deleteByUserIdAndMovieId(userId, movieId);
    }

    @Test
    void handleMovieRatingDeletedEvent_UserMovieRatingExists_DoesNotDelete() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        MovieRatingDeletedEvent event = MovieRatingDeletedEvent.builder()
            .userId(userId)
            .movieId(movieId)
            .build();
        when(userMovieRatingRepository.existsByUserIdAndMovieId(userId, movieId))
            .thenReturn(true);
        consumer.handleMovieRatingDeletedEvent(event);
        verify(userMovieRatingRepository, never()).deleteByUserIdAndMovieId(any(), any());
    }

    @Test
    void handleMovieRatingCreatedEvent_CategoryNotFound_ThrowsException() {
        UUID userId = UUID.randomUUID();
        UUID movieId = UUID.randomUUID();
        UUID missingCategory = UUID.randomUUID();
        RatingDetail detailEvent = RatingDetail.builder()
            .ratingCategoryId(missingCategory)
            .rating(3)
            .build();
        MovieRatingCreatedEvent event = MovieRatingCreatedEvent.builder()
            .userId(userId)
            .movieId(movieId)
            .ratingDetails(Collections.singletonList(detailEvent))
            .build();
        when(userMovieRatingRepository.existsByUserIdAndMovieId(userId, movieId)).thenReturn(false);
        Movie movie = Movie.builder().id(movieId).title("Test").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(userMovieRatingMapper.userMovieRatingCreatedEventToUserMovieRating(event, movie))
            .thenReturn(UserMovieRating.builder().userId(userId).movie(movie).build());
        when(ratingCategoryRepository.findByIdIn(Set.of(missingCategory)))
            .thenReturn(Collections.emptyList());
        assertThrows(CategoryNotFoundException.class, () -> consumer.handleMovieRatingCreatedEvent(event));
    }
}