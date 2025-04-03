package org.movierc.movieservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.dto.AssignFilmPersonRoleDto;
import org.movierc.movieservice.dto.RemoveFilmPersonRoleDto;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.service.MovieService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieFacadeTest {

    @Mock
    private MovieService movieService;

    @InjectMocks
    private MovieFacade movieFacade;

    @Test
    void getAllMovies_MoviesExist_ReturnsList() {
        // Setup
        MovieDto dto = MovieDto.builder()
                .id(UUID.randomUUID())
                .title("Test Movie")
                .build();
        when(movieService.getAllMovies()).thenReturn(List.of(dto));
        // Act
        List<MovieDto> result = movieFacade.getAllMovies();
        // Verify
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
        verify(movieService).getAllMovies();
    }

    @Test
    void getAllMovies_ServiceThrowsException_PropagatesException() {
        // Setup
        when(movieService.getAllMovies()).thenThrow(new RuntimeException("Error"));
        // Act & Verify
        assertThrows(RuntimeException.class, () -> movieFacade.getAllMovies());
    }

    @Test
    void getMovieById_MovieExists_ReturnsMovieDto() {
        // Setup
        UUID id = UUID.randomUUID();
        MovieDto dto = MovieDto.builder()
                .id(id)
                .title("Movie Title")
                .build();
        when(movieService.getMovieById(id)).thenReturn(dto);
        // Act
        MovieDto result = movieFacade.getMovieById(id);
        // Verify
        assertEquals(id, result.getId());
        verify(movieService).getMovieById(id);
    }

    @Test
    void getMovieById_MovieNotFound_ThrowsException() {
        // Setup
        UUID id = UUID.randomUUID();
        when(movieService.getMovieById(id)).thenThrow(new MovieNotFoundException(id));
        // Act & Verify
        assertThrows(MovieNotFoundException.class, () -> movieFacade.getMovieById(id));
        verify(movieService).getMovieById(id);
    }

    @Test
    void createMovie_ValidInput_ReturnsCreatedMovieDto() {
        // Setup
        MovieCreateDto createDto = new MovieCreateDto();
        createDto.setTitle("New Movie");

        MovieDto createdDto = MovieDto.builder()
                .id(UUID.randomUUID())
                .title("New Movie")
                .build();
        when(movieService.createMovie(createDto)).thenReturn(createdDto);
        // Act
        MovieDto result = movieFacade.createMovie(createDto);
        // Verify
        assertEquals("New Movie", result.getTitle());
        verify(movieService).createMovie(createDto);
    }

    @Test
    void createMovie_ServiceThrowsException_PropagatesException() {
        // Setup
        MovieCreateDto createDto = MovieCreateDto.builder()
                .title("Invalid Movie")
                .build();

        when(movieService.createMovie(createDto)).thenThrow(new RuntimeException("Creation error"));
        // Act & Verify
        assertThrows(RuntimeException.class, () -> movieFacade.createMovie(createDto));
        verify(movieService).createMovie(createDto);
    }

    @Test
    void updateMovie_MovieExists_ReturnsUpdatedMovieDto() {
        // Setup
        UUID id = UUID.randomUUID();
        MovieUpdateDto updateDto = new MovieUpdateDto();
        updateDto.setTitle("Updated Title");
        MovieDto updatedDto = MovieDto.builder()
                .id(id)
                .title("Updated Title")
                .build();
        when(movieService.updateMovie(id, updateDto)).thenReturn(updatedDto);
        // Act
        MovieDto result = movieFacade.updateMovie(id, updateDto);
        // Verify
        assertEquals("Updated Title", result.getTitle());
        verify(movieService).updateMovie(id, updateDto);
    }

    @Test
    void updateMovie_MovieNotFound_ThrowsException() {
        // Setup
        UUID id = UUID.randomUUID();
        MovieUpdateDto updateDto = new MovieUpdateDto();
        updateDto.setTitle("Updated Title");
        when(movieService.updateMovie(id, updateDto)).thenThrow(new MovieNotFoundException(id));
        // Act & Verify
        assertThrows(MovieNotFoundException.class, () -> movieFacade.updateMovie(id, updateDto));
        verify(movieService).updateMovie(id, updateDto);
    }

    @Test
    void deleteMovie_MovieExists_DeletesMovie() {
        // Setup
        UUID id = UUID.randomUUID();
        doNothing().when(movieService).deleteMovie(id);
        // Act
        movieFacade.deleteMovie(id);
        // Verify
        verify(movieService).deleteMovie(id);
    }

    @Test
    void deleteMovie_MovieNotFound_ThrowsException() {
        // Setup
        UUID id = UUID.randomUUID();
        doThrow(new MovieNotFoundException(id)).when(movieService).deleteMovie(id);
        // Act & Verify
        assertThrows(MovieNotFoundException.class, () -> movieFacade.deleteMovie(id));
        verify(movieService).deleteMovie(id);
    }

    @Test
    void assignGenre_MovieAndGenreExist_ReturnsUpdatedMovieDto() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        MovieDto updatedDto = MovieDto.builder()
                .id(movieId)
                .title("Movie with Genre")
                .build();
        when(movieService.assignGenre(movieId, genreId)).thenReturn(updatedDto);
        // Act
        MovieDto result = movieFacade.assignGenre(movieId, genreId);
        // Verify
        assertEquals(movieId, result.getId());
        verify(movieService).assignGenre(movieId, genreId);
    }

    @Test
    void assignGenre_ServiceThrowsException_PropagatesException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieService.assignGenre(movieId, genreId))
                .thenThrow(new RuntimeException("Assign genre error"));
        // Act & Verify
        assertThrows(RuntimeException.class, () -> movieFacade.assignGenre(movieId, genreId));
        verify(movieService).assignGenre(movieId, genreId);
    }

    @Test
    void removeGenre_MovieExists_RemovesGenreAndReturnsUpdatedMovieDto() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        MovieDto updatedDto = MovieDto.builder()
                .id(movieId)
                .title("Movie without Genre")
                .build();
        when(movieService.removeGenre(movieId, genreId)).thenReturn(updatedDto);
        // Act
        MovieDto result = movieFacade.removeGenre(movieId, genreId);
        // Verify
        assertEquals(movieId, result.getId());
        verify(movieService).removeGenre(movieId, genreId);
    }

    @Test
    void removeGenre_MovieNotFound_ThrowsException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieService.removeGenre(movieId, genreId)).thenThrow(new MovieNotFoundException(movieId));
        // Act & Verify
        assertThrows(MovieNotFoundException.class, () -> movieFacade.removeGenre(movieId, genreId));
        verify(movieService).removeGenre(movieId, genreId);
    }

    @Test
    void assignFilmPerson_MovieAndFilmPersonExist_ReturnsUpdatedMovieDto() {
        // Setup
        UUID movieId = UUID.randomUUID();
        AssignFilmPersonRoleDto assignDto = new AssignFilmPersonRoleDto();
        assignDto.setFilmPersonId(UUID.randomUUID());
        MovieDto updatedDto = MovieDto.builder()
                .id(movieId)
                .title("Movie with FilmPerson")
                .build();
        when(movieService.assignFilmPerson(movieId, assignDto)).thenReturn(updatedDto);
        // Act
        MovieDto result = movieFacade.assignFilmPerson(movieId, assignDto);
        // Verify
        assertEquals(movieId, result.getId());
        verify(movieService).assignFilmPerson(movieId, assignDto);
    }

    @Test
    void assignFilmPerson_ServiceThrowsException_PropagatesException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        AssignFilmPersonRoleDto assignDto = new AssignFilmPersonRoleDto();
        assignDto.setFilmPersonId(UUID.randomUUID());
        when(movieService.assignFilmPerson(movieId, assignDto))
                .thenThrow(new RuntimeException("Assign film person error"));
        // Act & Verify
        assertThrows(RuntimeException.class, () -> movieFacade.assignFilmPerson(movieId, assignDto));
        verify(movieService).assignFilmPerson(movieId, assignDto);
    }

    @Test
    void removeFilmPerson_MovieExists_RemovesFilmPersonAndReturnsUpdatedMovieDto() {
        // Setup
        UUID movieId = UUID.randomUUID();
        RemoveFilmPersonRoleDto removeDto = new RemoveFilmPersonRoleDto();
        removeDto.setFilmPersonId(UUID.randomUUID());
        MovieDto updatedDto = MovieDto.builder()
                .id(movieId)
                .title("Movie after removal")
                .build();
        when(movieService.removeFilmPerson(movieId, removeDto)).thenReturn(updatedDto);
        // Act
        MovieDto result = movieFacade.removeFilmPerson(movieId, removeDto);
        // Verify
        assertEquals(movieId, result.getId());
        verify(movieService).removeFilmPerson(movieId, removeDto);
    }

    @Test
    void removeFilmPerson_MovieNotFound_ThrowsException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        RemoveFilmPersonRoleDto removeDto = new RemoveFilmPersonRoleDto();
        removeDto.setFilmPersonId(UUID.randomUUID());
        when(movieService.removeFilmPerson(movieId, removeDto)).thenThrow(new MovieNotFoundException(movieId));
        // Act & Verify
        assertThrows(MovieNotFoundException.class, () -> movieFacade.removeFilmPerson(movieId, removeDto));
        verify(movieService).removeFilmPerson(movieId, removeDto);
    }
}
