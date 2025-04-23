package org.movierc.recommendationservice.rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieDeletedEvent;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.mapper.MovieEventMapper;
import org.movierc.recommendationservice.repository.MovieRepository;

import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieEventConsumerTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private MovieEventMapper movieEventMapper;

    private MovieEventConsumer movieEventConsumer;

    @BeforeEach
    void setUp() {
        movieEventConsumer = new MovieEventConsumer(movieRepository, movieEventMapper);
    }

    @Test
    void handleMovieCreatedEvent_MovieNotExists_SavesMovie() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieCreatedEvent event = MovieCreatedEvent.builder()
                .id(movieId)
                .title("Test Movie")
                .build();
        Movie movie = Movie.builder()
                .id(movieId)
                .title("Test Movie")
                .build();

        when(movieRepository.existsById(movieId)).thenReturn(false);
        when(movieEventMapper.movieCreatedEventToMovie(event)).thenReturn(movie);

        // Act
        movieEventConsumer.handleMovieCreatedEvent(event);

        // Verify
        verify(movieRepository, times(1)).save(movie);
    }

    @Test
    void handleMovieCreatedEvent_MovieExists_DoesNotSaveMovie() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieCreatedEvent event = MovieCreatedEvent.builder()
                .id(movieId)
                .title("Test Movie")
                .build();

        when(movieRepository.existsById(movieId)).thenReturn(true);

        // Act
        movieEventConsumer.handleMovieCreatedEvent(event);

        // Verify
        verify(movieRepository, never()).save(any());
    }

    @Test
    void handleMovieUpdatedEvent_MovieExists_UpdatesMovie() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieUpdatedEvent event = MovieUpdatedEvent.builder()
                .id(movieId)
                .title("Updated Movie")
                .build();
        Movie originalMovie = Movie.builder()
                .id(movieId)
                .title("Original Movie")
                .build();

        when(movieRepository.findById(movieId)).thenReturn(Optional.of(originalMovie));

        // Act
        movieEventConsumer.handleMovieUpdatedEvent(event);

        // Verify
        verify(movieRepository, times(1))
                .save(argThat(
                        m -> m.getId().equals(movieId) && m.getTitle().equals("Updated Movie"))
                );
    }

    @Test
    void handleMovieUpdatedEvent_MovieNotExists_DoesNotUpdateMovie() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieUpdatedEvent event = MovieUpdatedEvent.builder()
                .id(movieId)
                .title("Updated Movie")
                .build();

        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        // Act
        movieEventConsumer.handleMovieUpdatedEvent(event);

        // Verify
        verify(movieRepository, never()).save(any());
    }

    @Test
    void handleMovieDeletedEvent_MovieExists_DeletesMovie() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieDeletedEvent event = MovieDeletedEvent.builder()
                .id(movieId)
                .build();

        when(movieRepository.existsById(movieId)).thenReturn(true);

        // Act
        movieEventConsumer.handleMovieDeletedEvent(event);

        // Verify
        verify(movieRepository, times(1)).deleteById(movieId);
    }

    @Test
    void handleMovieDeletedEvent_MovieNotExists_DoesNotDeleteMovie() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieDeletedEvent event = MovieDeletedEvent.builder()
                .id(movieId)
                .build();

        when(movieRepository.existsById(movieId)).thenReturn(false);

        // Act
        movieEventConsumer.handleMovieDeletedEvent(event);

        // Verify
        verify(movieRepository, never()).deleteById(any());
    }
}