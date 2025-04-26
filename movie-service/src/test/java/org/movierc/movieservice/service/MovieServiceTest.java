package org.movierc.movieservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.entity.FilmPersonInMovieRole;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.entity.Movie;
import org.movierc.movieservice.exception.FilmPersonNotFoundException;
import org.movierc.movieservice.exception.FilmPersonWithThisRoleIsAlreadyAssigned;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.repository.FilmPersonRepository;
import org.movierc.movieservice.repository.GenreRepository;
import org.movierc.movieservice.repository.MovieRepository;
import org.movierc.shared.enm.FilmPersonRole;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;
    @Mock
    private FilmPersonRepository filmPersonRepository;
    @Mock
    private GenreRepository genreRepository;

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        movieService = new MovieService(genreRepository, filmPersonRepository, movieRepository);
    }

    @Test
    void getAllMovies_WhenMoviesExist_ReturnsListOfMovies() {
        Movie movie = Movie.builder().id(UUID.randomUUID()).title("Test Movie").build();
        when(movieRepository.findAll()).thenReturn(List.of(movie));

        List<Movie> result = movieService.getAllMovies();

        assertEquals(1, result.size());
        assertEquals(movie.getId(), result.getFirst().getId());
    }

    @Test
    void getAllMovies_WhenNoMoviesExist_ReturnsEmptyList() {
        when(movieRepository.findAll()).thenReturn(List.of());

        List<Movie> result = movieService.getAllMovies();

        assertTrue(result.isEmpty());
    }

    @Test
    void getMovieById_MovieExists_ReturnsMovie() {
        UUID movieId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Test Movie").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        Movie result = movieService.getMovieById(movieId);

        assertNotNull(result);
        assertEquals(movieId, result.getId());
    }

    @Test
    void getMovieById_MovieDoesNotExist_ThrowsMovieNotFoundException() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieById(movieId));
    }

    @Test
    void createMovie_ValidInput_CreatesMovie() {
        UUID genreId = UUID.randomUUID();
        List<UUID> genreIds = List.of(genreId);
        Map<UUID, FilmPersonRole> filmPersonRoles = Map.of();

        Movie movieParam = Movie.builder()
                .title("New Movie")
                .description("Awesome movie")
                .images(List.of())
                .build();

        Genre genre = Genre.builder().id(genreId).name("Action").build();
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));

        Movie savedMovie = Movie.builder()
                .id(UUID.randomUUID())
                .title(movieParam.getTitle())
                .description(movieParam.getDescription())
                .images(movieParam.getImages())
                .genres(List.of(genre))
                .filmPersonRoles(List.of())
                .build();
        when(movieRepository.save(any(Movie.class))).thenReturn(savedMovie);

        Movie result = movieService.createMovie(movieParam, genreIds, filmPersonRoles);

        verify(movieRepository, times(1)).save(any(Movie.class));
        assertNotNull(result);
        assertEquals(savedMovie.getTitle(), result.getTitle());
        assertEquals(1, result.getGenres().size());
        assertEquals(genreId, result.getGenres().getFirst().getId());
    }

    @Test
    void createMovie_InvalidGenre_ThrowsGenreNotFoundException() {
        UUID genreId = UUID.randomUUID();
        List<UUID> genreIds = List.of(genreId);
        Movie movieParam = Movie.builder()
                .title("New Movie")
                .description("Awesome movie")
                .images(List.of())
                .build();
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class,
                () -> movieService.createMovie(movieParam, genreIds, Map.of()));
        verify(movieRepository, never()).save(any());
    }

    @Test
    void createMovie_FilmPersonNotFound_ThrowsFilmPersonNotFoundException() {
        UUID genreId = UUID.randomUUID();
        List<UUID> genreIds = List.of(genreId);
        UUID filmPersonId = UUID.randomUUID();
        Map<UUID, FilmPersonRole> filmPersonRoles = Map.of(filmPersonId, FilmPersonRole.ACTOR);

        Movie movieParam = Movie.builder()
                .title("New Movie")
                .description("Awesome movie")
                .images(List.of())
                .build();
        Genre genre = Genre.builder().id(genreId).name("Action").build();
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.empty());

        assertThrows(FilmPersonNotFoundException.class,
                () -> movieService.createMovie(movieParam, genreIds, filmPersonRoles));
        verify(movieRepository, never()).save(any());
    }

    @Test
    void updateMovie_MovieExists_UpdatesMovie() {
        UUID movieId = UUID.randomUUID();
        Movie existingMovie = Movie.builder()
                .id(movieId)
                .title("Old Title")
                .description("Old description")
                .images(List.of())
                .build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String newTitle = "Updated Title";
        String newDescription = "Updated description";
        URL url = mock(URL.class);
        List<URL> newImages = List.of(url);

        Movie result = movieService.updateMovie(movieId, newTitle, newDescription, newImages);

        assertNotNull(result);
        assertEquals(newTitle, result.getTitle());
        assertEquals(newDescription, result.getDescription());
        assertEquals(newImages, result.getImages());
    }

    @Test
    void updateMovie_MovieNotFound_ThrowsMovieNotFoundException() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class,
                () -> movieService.updateMovie(movieId, "Title", "Desc", List.of()));
    }

    @Test
    void deleteMovie_MovieExists_DeletesMovie() {
        UUID movieId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Delete Movie").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        Movie result = movieService.deleteMovie(movieId);

        verify(movieRepository).deleteById(movieId);
        assertEquals(movie, result);
    }

    @Test
    void deleteMovie_MovieNotFound_ThrowsMovieNotFoundException() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class,
                () -> movieService.deleteMovie(movieId));
        verify(movieRepository, never()).deleteById(any());
    }

    @Test
    void assignGenre_MovieAndGenreExist_AssignsGenre() {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Movie").genres(new ArrayList<>()).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        Genre genre = Genre.builder().id(genreId).name("Thriller").build();
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie result = movieService.assignGenre(movieId, genreId);

        assertTrue(result.getGenres().stream().anyMatch(g -> g.getId().equals(genreId)));
    }

    @Test
    void assignGenre_MovieNotFound_ThrowsMovieNotFoundException() {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class,
                () -> movieService.assignGenre(movieId, genreId));
    }

    @Test
    void assignGenre_GenreNotFound_ThrowsGenreNotFoundException() {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Movie").genres(new ArrayList<>()).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());

        assertThrows(GenreNotFoundException.class,
                () -> movieService.assignGenre(movieId, genreId));
    }

    @Test
    void removeGenre_SuccessfullyRemovesGenre() {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        Genre genre = Genre.builder().id(genreId).name("Comedy").build();
        Movie movie = Movie.builder().id(movieId).title("Movie").genres(new ArrayList<>(List.of(genre))).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie result = movieService.removeGenre(movieId, genreId);

        assertTrue(result.getGenres().isEmpty());
    }

    @Test
    void removeGenre_MovieNotFound_ThrowsMovieNotFoundException() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class,
                () -> movieService.removeGenre(movieId, UUID.randomUUID()));
    }

    @Test
    void assignFilmPerson_MovieAndFilmPersonExist_AssignsPerson() {
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(new ArrayList<>()).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));

        FilmPerson filmPerson = FilmPerson.builder().id(filmPersonId).build();
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie result = movieService.assignFilmPerson(movieId, filmPersonId, FilmPersonRole.ACTOR);

        assertTrue(result.getFilmPersonRoles().stream().anyMatch(fpr ->
                fpr.getFilmPerson().getId().equals(filmPersonId) && fpr.getRole() == FilmPersonRole.ACTOR));
    }

    @Test
    void assignFilmPerson_AlreadyAssigned_ThrowsFilmPersonWithThisRoleIsAlreadyAssigned() {
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        FilmPerson filmPerson = FilmPerson.builder().id(filmPersonId).build();
        FilmPersonInMovieRole existingRole = new FilmPersonInMovieRole(UUID.randomUUID(), null, filmPerson, FilmPersonRole.ACTOR);
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(new ArrayList<>(List.of(existingRole))).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));

        assertThrows(FilmPersonWithThisRoleIsAlreadyAssigned.class,
                () -> movieService.assignFilmPerson(movieId, filmPersonId, FilmPersonRole.ACTOR));
        verify(movieRepository, never()).save(any());
    }

    @Test
    void assignFilmPerson_MovieNotFound_ThrowsMovieNotFoundException() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class,
                () -> movieService.assignFilmPerson(movieId, UUID.randomUUID(), FilmPersonRole.ACTOR));
    }

    @Test
    void assignFilmPerson_FilmPersonNotFound_ThrowsFilmPersonNotFoundException() {
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(new ArrayList<>()).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.empty());

        assertThrows(FilmPersonNotFoundException.class,
                () -> movieService.assignFilmPerson(movieId, filmPersonId, FilmPersonRole.ACTOR));
        verify(movieRepository, never()).save(any());
    }

    @Test
    void removeFilmPerson_RemovesCorrectRoles() {
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        FilmPerson filmPerson = FilmPerson.builder().id(filmPersonId).build();
        FilmPersonInMovieRole role = new FilmPersonInMovieRole(UUID.randomUUID(), null, filmPerson, FilmPersonRole.ACTOR);
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(new ArrayList<>(List.of(role))).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Movie result = movieService.removeFilmPerson(movieId, filmPersonId, FilmPersonRole.ACTOR);

        assertEquals(1, result.getFilmPersonRoles().size());
        assertEquals(FilmPersonRole.ACTOR, result.getFilmPersonRoles().getFirst().getRole());
    }

    @Test
    void removeFilmPerson_MovieNotFound_ThrowsMovieNotFoundException() {
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());

        assertThrows(MovieNotFoundException.class,
                () -> movieService.removeFilmPerson(movieId, UUID.randomUUID(), FilmPersonRole.ACTOR));
    }

    @Test
    void removeFilmPerson_FilmPersonNotFound_ThrowsFilmPersonNotFoundException() {
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(new ArrayList<>()).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.empty());

        assertThrows(FilmPersonNotFoundException.class,
                () -> movieService.removeFilmPerson(movieId, filmPersonId, FilmPersonRole.ACTOR));
        verify(movieRepository, never()).save(any());
    }
}
