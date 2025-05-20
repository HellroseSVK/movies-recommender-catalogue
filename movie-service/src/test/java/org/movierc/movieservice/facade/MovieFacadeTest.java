package org.movierc.movieservice.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.movie.MovieFilmPersonRoleAddedEvent;
import org.movierc.events.movie.MovieFilmPersonRoleRemovedEvent;
import org.movierc.movieservice.dto.AssignFilmPersonRoleDto;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.dto.RemoveFilmPersonRoleDto;
import org.movierc.movieservice.entity.Movie;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.mapper.MovieCreateDtoMapperImpl;
import org.movierc.movieservice.mapper.MovieMapperImpl;
import org.movierc.movieservice.rabbit.MovieEventProducer;
import org.movierc.movieservice.rabbit.MovieFilmPersonRoleProducer;
import org.movierc.movieservice.service.MovieService;
import org.movierc.shared.enm.FilmPersonRole;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieFacadeTest {

    @Mock
    private MovieService movieService;

    @Mock
    private MovieEventProducer movieEventProducer;

    @Mock
    private MovieFilmPersonRoleProducer movieFilmPersonRoleProducer;

    private MovieFacade movieFacade;

    @BeforeEach
    void setUp() {
        movieFacade = new MovieFacade(movieService, movieEventProducer, movieFilmPersonRoleProducer, new MovieMapperImpl(), new MovieCreateDtoMapperImpl());
    }

    @Test
    void getAllMovies_MoviesExist_ReturnsListOfMovieDtos() {
        // Setup
        Movie movie = Movie.builder().id(UUID.randomUUID()).title("Test Movie").build();
        when(movieService.getAllMovies()).thenReturn(List.of(movie));

        // Act
        List<MovieDto> result = movieFacade.getAllMovies();

        // Verify
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.getFirst().getTitle());
        verify(movieService).getAllMovies();
    }

    @Test
    void getAllMovies_NoMoviesExist_ReturnsEmptyList() {
        // Setup
        when(movieService.getAllMovies()).thenReturn(List.of());

        // Act
        List<MovieDto> result = movieFacade.getAllMovies();

        // Verify
        assertTrue(result.isEmpty());
        verify(movieService).getAllMovies();
    }

    @Test
    void getMovieById_MovieExists_ReturnsMovieDto() {
        // Setup
        UUID movieId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Test Movie").build();
        when(movieService.getMovieById(movieId)).thenReturn(movie);

        // Act
        MovieDto result = movieFacade.getMovieById(movieId);

        // Verify
        assertEquals(movieId, result.getId());
        assertEquals("Test Movie", result.getTitle());
        verify(movieService).getMovieById(movieId);
    }

    @Test
    void getMovieById_MovieNotFound_ThrowsException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        when(movieService.getMovieById(movieId)).thenThrow(new MovieNotFoundException(movieId));

        // Act & Verify
        assertThrows(MovieNotFoundException.class, () -> movieFacade.getMovieById(movieId));
        verify(movieService).getMovieById(movieId);
    }

    @Test
    void createMovie_ValidInput_ReturnsCreatedMovieDtoAndSendsEvent() {
        // Setup
        MovieCreateDto createDto = MovieCreateDto.builder().title("New Movie").filmPersonRoles(new ArrayList<>()).build();
        Movie movie = Movie.builder().id(UUID.randomUUID()).title("New Movie").filmPersonRoles(new ArrayList<>()).build();
        when(movieService.createMovie(any(), any(), any())).thenReturn(movie);

        // Act
        MovieDto result = movieFacade.createMovie(createDto);

        // Verify
        assertEquals("New Movie", result.getTitle());
        verify(movieService).createMovie(any(), any(), any());
        verify(movieEventProducer).sendMovieCreatedEvent(any());
    }

    @Test
    void updateMovie_ValidInput_ReturnsUpdatedMovieDtoAndSendsEvent() {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieUpdateDto updateDto = new MovieUpdateDto();
        updateDto.setTitle("Updated Title");
        updateDto.setDescription("Updated Description");
        updateDto.setImages(List.of());
        Movie updatedMovie = Movie.builder().id(movieId).title("Updated Title").build();
        when(movieService.updateMovie(movieId, updateDto.getTitle(), updateDto.getDescription(), updateDto.getImages())).thenReturn(updatedMovie);

        // Act
        MovieDto result = movieFacade.updateMovie(movieId, updateDto);

        // Verify
        assertEquals("Updated Title", result.getTitle());
        verify(movieService).updateMovie(movieId, updateDto.getTitle(), updateDto.getDescription(), updateDto.getImages());
        verify(movieEventProducer).sendMovieUpdatedEvent(any());
    }

    @Test
    void deleteMovie_ValidInput_DeletesMovieAndSendsEvent() {
        // Setup
        UUID movieId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Deleted Movie").build();
        when(movieService.deleteMovie(movieId)).thenReturn(movie);

        // Act
        movieFacade.deleteMovie(movieId);

        // Verify
        verify(movieService).deleteMovie(movieId);
        verify(movieEventProducer).sendMovieDeletedEvent(any());
    }

    @Test
    void assignFilmPerson_ValidInput_AssignsFilmPersonAndSendsEvent() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        AssignFilmPersonRoleDto assignDto = new AssignFilmPersonRoleDto();
        assignDto.setFilmPersonId(filmPersonId);
        assignDto.setRole(FilmPersonRole.ACTOR);
        Movie movie = Movie.builder().id(movieId).title("Movie with Actor").build();
        when(movieService.assignFilmPerson(movieId, assignDto.getFilmPersonId(), assignDto.getRole())).thenReturn(movie);

        // Act
        MovieDto result = movieFacade.assignFilmPerson(movieId, assignDto);

        // Verify
        assertEquals(movieId, result.getId());
        verify(movieService).assignFilmPerson(movieId, assignDto.getFilmPersonId(), assignDto.getRole());
        ArgumentCaptor<MovieFilmPersonRoleAddedEvent> eventCaptor = ArgumentCaptor.forClass(MovieFilmPersonRoleAddedEvent.class);
        verify(movieFilmPersonRoleProducer).sendMovieFilmPersonRoleAddedEvent(eventCaptor.capture());
        assertEquals(movieId, eventCaptor.getValue().getAddedMovieFilmPersonRole().getMovieId());
        assertEquals(filmPersonId, eventCaptor.getValue().getAddedMovieFilmPersonRole().getFilmPersonId());
        assertEquals(FilmPersonRole.ACTOR, eventCaptor.getValue().getAddedMovieFilmPersonRole().getRole());
    }

    @Test
    void removeFilmPerson_ValidInput_RemovesFilmPersonAndSendsEvent() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        RemoveFilmPersonRoleDto removeDto = new RemoveFilmPersonRoleDto();
        removeDto.setFilmPersonId(filmPersonId);
        removeDto.setRole(FilmPersonRole.DIRECTOR);
        Movie movie = Movie.builder().id(movieId).title("Movie without Director").build();
        when(movieService.removeFilmPerson(movieId, removeDto.getFilmPersonId(), removeDto.getRole())).thenReturn(movie);

        // Act
        MovieDto result = movieFacade.removeFilmPerson(movieId, removeDto);

        // Verify
        assertEquals(movieId, result.getId());
        verify(movieService).removeFilmPerson(movieId, removeDto.getFilmPersonId(), removeDto.getRole());
        ArgumentCaptor<MovieFilmPersonRoleRemovedEvent> eventCaptor = ArgumentCaptor.forClass(MovieFilmPersonRoleRemovedEvent.class);
        verify(movieFilmPersonRoleProducer).sendMovieFilmPersonRoleRemovedEvent(eventCaptor.capture());
        assertEquals(movieId, eventCaptor.getValue().getRemovedMovieFilmPersonRole().getMovieId());
        assertEquals(filmPersonId, eventCaptor.getValue().getRemovedMovieFilmPersonRole().getFilmPersonId());
        assertEquals(FilmPersonRole.DIRECTOR, eventCaptor.getValue().getRemovedMovieFilmPersonRole().getRole());
    }
}
