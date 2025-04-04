package org.movierc.filmpersonservice.rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieFilmPersonRole;
import org.movierc.events.movie.MovieFilmPersonRoleAddedEvent;
import org.movierc.events.movie.MovieFilmPersonRoleRemovedEvent;
import org.movierc.events.movie.MovieUpdatedEvent;
import org.movierc.filmpersonservice.entity.FilmPerson;
import org.movierc.filmpersonservice.entity.FilmPersonMovieAssignment;
import org.movierc.filmpersonservice.mapper.FilmPersonMovieAssignmentMapper;
import org.movierc.filmpersonservice.mapper.MovieEventMapper;
import org.movierc.filmpersonservice.repository.FilmPersonRepository;
import org.movierc.filmpersonservice.repository.MovieRepository;
import org.movierc.shared.enm.FilmPersonRole;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class MovieEventConsumerTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private FilmPersonRepository filmPersonRepository;

    private final MovieEventMapper movieEventMapper = Mappers.getMapper(MovieEventMapper.class);
    private final FilmPersonMovieAssignmentMapper filmPersonMovieAssignmentMapper = Mappers.getMapper(FilmPersonMovieAssignmentMapper.class);

    private MovieEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new MovieEventConsumer(movieRepository, filmPersonRepository, movieEventMapper, filmPersonMovieAssignmentMapper);
    }

    @Test
    void handleMovieCreatedEvent_MovieNotExistsAndFilmPersonExists_AssignmentsUpdated() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        MovieFilmPersonRole role = MovieFilmPersonRole.builder()
                .movieId(movieId)
                .filmPersonId(filmPersonId)
                .role(null)
                .build();
        MovieCreatedEvent event = MovieCreatedEvent.builder()
                .id(movieId)
                .title("Test")
                .description("Desc")
                .images(new ArrayList<>())
                .genres(new ArrayList<>())
                .filmPersonRoles(new ArrayList<>())
                .build();
        event.getFilmPersonRoles().add(role);

        var filmPerson = new FilmPerson();
        filmPerson.setId(filmPersonId);
        filmPerson.setMovies(new ArrayList<>());

        when(movieRepository.existsById(movieId)).thenReturn(false);
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));

        // Act
        consumer.handleMovieCreatedEvent(event);

        // Verify
        verify(movieRepository, times(1)).save(any());
        verify(filmPersonRepository, times(1)).save(filmPerson);
        // Check that assignment is added
        assertFalse(filmPerson.getMovies().isEmpty());
    }

    @Test
    void handleMovieCreatedEvent_MovieNotExistsAndFilmPersonNotFound_NoAssignmentUpdated() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        MovieFilmPersonRole role = MovieFilmPersonRole.builder()
                .movieId(movieId)
                .filmPersonId(filmPersonId)
                .role(null)
                .build();
        MovieCreatedEvent event = MovieCreatedEvent.builder()
                .id(movieId)
                .title("Test")
                .description("Desc")
                .images(new ArrayList<>())
                .genres(new ArrayList<>())
                .filmPersonRoles(new ArrayList<>())
                .build();
        event.getFilmPersonRoles().add(role);

        when(movieRepository.existsById(movieId)).thenReturn(false);
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.empty());

        // Act
        consumer.handleMovieCreatedEvent(event);

        // Verify
        verify(movieRepository, times(1)).save(any());
        verify(filmPersonRepository, never()).save(any());
    }

    @Test
    void handleMovieUpdatedEvent_MovieExists_UpdatesMovie() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieUpdatedEvent event = MovieUpdatedEvent.builder()
                .id(movieId)
                .title("UpdateTest")
                .description("UpdateDesc")
                .images(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        when(movieRepository.existsById(movieId)).thenReturn(true);

        // Act
        consumer.handleMovieUpdatedEvent(event);

        // Verify
        verify(movieRepository, times(1)).save(any());
    }

    @Test
    void handleMovieUpdatedEvent_MovieNotExists_NoUpdateCall() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieUpdatedEvent event = MovieUpdatedEvent.builder()
                .id(movieId)
                .title("UpdateTest")
                .description("UpdateDesc")
                .images(new ArrayList<>())
                .genres(new ArrayList<>())
                .build();
        when(movieRepository.existsById(movieId)).thenReturn(false);

        // Act
        consumer.handleMovieUpdatedEvent(event);

        // Verify
        verify(movieRepository, never()).save(any());
    }

    @Test
    void handleMovieDeletedEvent_MovieExists_FilmPersonUpdated() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        MovieCreatedEvent event = MovieCreatedEvent.builder()
                .id(movieId)
                .title("DeleteTest")
                .description("DeleteDesc")
                .images(new ArrayList<>())
                .genres(new ArrayList<>())
                .filmPersonRoles(new ArrayList<>())
                .build();
        MovieFilmPersonRole role = MovieFilmPersonRole.builder()
                .movieId(movieId)
                .filmPersonId(filmPersonId)
                .role(null)
                .build();
        event.getFilmPersonRoles().add(role);

        FilmPerson filmPerson = new FilmPerson();
        // Simulate an assignment that will be removed
        filmPerson.setMovies(new ArrayList<>());
        var filmPersonMovieAssignment = new FilmPersonMovieAssignment(movieId, FilmPersonRole.ACTOR);
        filmPerson.getMovies().add(filmPersonMovieAssignment);

        when(movieRepository.existsById(movieId)).thenReturn(true);
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));

        // Act
        consumer.handleMovieDeletedEvent(event);

        // Verify
        verify(movieRepository, times(1)).deleteById(movieId);
        verify(filmPersonRepository, times(1)).save(filmPerson);
        // Verify removal: assignment list should be empty now.
        assertTrue(filmPerson.getMovies().isEmpty());
    }

    @Test
    void handleMovieDeletedEvent_MovieNotExists_NoDeletion() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieCreatedEvent event = MovieCreatedEvent.builder()
                .id(movieId)
                .title("DeleteTest")
                .description("DeleteDesc")
                .images(new ArrayList<>())
                .genres(new ArrayList<>())
                .filmPersonRoles(new ArrayList<>())
                .build();
        when(movieRepository.existsById(movieId)).thenReturn(false);

        // Act
        consumer.handleMovieDeletedEvent(event);

        // Verify
        verify(movieRepository, never()).deleteById(movieId);
    }

    @Test
    void handleMovieFilmPersonRoleAddedEvent_FilmPersonExists_AssignmentAdded() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();

        MovieFilmPersonRole role = MovieFilmPersonRole.builder()
                .movieId(movieId)
                .filmPersonId(filmPersonId)
                .role(null)
                .build();
        MovieFilmPersonRoleAddedEvent event = MovieFilmPersonRoleAddedEvent.builder()
                .addedMovieFilmPersonRole(role)
                .build();

        FilmPerson filmPerson = new FilmPerson();
        filmPerson.setId(filmPersonId);
        filmPerson.setMovies(new ArrayList<>());
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));

        // Act
        consumer.handleMovieFilmPersonRoleAddedEvent(event);

        // Verify
        verify(filmPersonRepository, times(1)).save(filmPerson);
        assert (!filmPerson.getMovies().isEmpty());
    }

    @Test
    void handleMovieFilmPersonRolesAddedEvent_FilmPersonNotExists_NoAssignmentAdded() {
        // Setup
        UUID filmPersonId = UUID.randomUUID();

        MovieFilmPersonRole role = MovieFilmPersonRole.builder()
                .movieId(UUID.randomUUID())
                .filmPersonId(filmPersonId)
                .role(null)
                .build();
        MovieFilmPersonRoleAddedEvent event = MovieFilmPersonRoleAddedEvent.builder()
                .addedMovieFilmPersonRole(role)
                .build();

        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.empty());

        // Act
        consumer.handleMovieFilmPersonRoleAddedEvent(event);

        // Verify
        verify(filmPersonRepository, never()).save(any());
    }

    @Test
    void handleMovieFilmPersonRolesRemovedEvent_FilmPersonExists_AssignmentRemoved() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        MovieFilmPersonRole role = MovieFilmPersonRole.builder()
                // Note: based on consumer code, removal is done by comparing film person id with assignment movieId.
                .movieId(movieId)
                .filmPersonId(filmPersonId)
                .role(null)
                .build();
        MovieFilmPersonRoleRemovedEvent event = MovieFilmPersonRoleRemovedEvent.builder()
                .removedMovieFilmPersonRole(role)
                .build();


        // Create film person with an assignment whose getMovieId equals role.filmPersonId (matches removal condition)
        FilmPerson filmPerson = new FilmPerson();
        filmPerson.setId(filmPersonId);
        filmPerson.setMovies(new ArrayList<>());
        var filmPersonMovieAssignment = new FilmPersonMovieAssignment(movieId, FilmPersonRole.ACTOR);
        filmPerson.getMovies().add(filmPersonMovieAssignment);
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));

        // Act
        consumer.handleMovieFilmPersonRoleRemovedEvent(event);

        // Verify
        verify(filmPersonRepository, times(1)).save(filmPerson);
        assertTrue(filmPerson.getMovies().isEmpty());
    }

    @Test
    void handleMovieFilmPersonRolesRemovedEvent_FilmPersonNotExists_NoAssignmentRemoved() {
        // Setup
        UUID filmPersonId = UUID.randomUUID();

        MovieFilmPersonRole role = MovieFilmPersonRole.builder()
                .movieId(UUID.randomUUID())
                .filmPersonId(filmPersonId)
                .role(null)
                .build();
        MovieFilmPersonRoleRemovedEvent event = MovieFilmPersonRoleRemovedEvent.builder()
                .removedMovieFilmPersonRole(role)
                .build();

        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.empty());

        // Act
        consumer.handleMovieFilmPersonRoleRemovedEvent(event);

        // Verify
        verify(filmPersonRepository, never()).save(any());
    }
}
