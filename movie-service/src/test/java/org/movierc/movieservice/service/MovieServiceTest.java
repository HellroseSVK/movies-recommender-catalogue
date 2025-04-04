package org.movierc.movieservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.movieservice.dto.AssignFilmPersonRoleDto;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.dto.MovieFilmPersonRoleCreateDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.dto.RemoveFilmPersonRoleDto;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.entity.FilmPersonInMovieRole;
import org.movierc.movieservice.entity.Genre;
import org.movierc.movieservice.entity.Movie;
import org.movierc.movieservice.exception.FilmPersonNotFoundException;
import org.movierc.movieservice.exception.FilmPersonWithThisRoleIsAlreadyAssigned;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.mapper.GenreMapper;
import org.movierc.movieservice.mapper.MovieCreateDtoMapper;
import org.movierc.movieservice.mapper.MovieMapper;
import org.movierc.movieservice.rabbit.MovieEventProducer;
import org.movierc.movieservice.rabbit.MovieFilmPersonRoleProducer;
import org.movierc.movieservice.repository.FilmPersonRepository;
import org.movierc.movieservice.repository.GenreRepository;
import org.movierc.movieservice.repository.MovieRepository;
import org.movierc.shared.enm.FilmPersonRole;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
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
    @Mock
    private MovieEventProducer movieEventProducer;

    @Mock
    private MovieFilmPersonRoleProducer movieFilmPersonRoleProducer;
    
    private final MovieCreateDtoMapper movieCreateDtoMapper = Mappers.getMapper(MovieCreateDtoMapper.class);
    private final MovieMapper movieMapper = Mappers.getMapper(MovieMapper.class);
    private final GenreMapper genreMapper = Mappers.getMapper(GenreMapper.class);

    
    private MovieService movieService;
    
    @BeforeEach
    void setUp() {
        movieService = new MovieService(genreRepository, filmPersonRepository, movieRepository, movieEventProducer, movieFilmPersonRoleProducer, movieMapper, movieCreateDtoMapper);
    }
    
    @Test
    void getAllMovies_WhenMoviesExist_ReturnsListOfMovieDtos() {
        // Setup
        Movie movie = Movie.builder()
                .id(UUID.randomUUID())
                .title("Test Movie")
                .build();
        when(movieRepository.findAll()).thenReturn(List.of(movie));
        
        // Act
        List<MovieDto> result = movieService.getAllMovies();
        
        // Verify
        assertEquals(1, result.size());
        assertEquals(movie.getId(), result.get(0).getId());
    }

    @Test
    void getAllMovies_WhenNoMoviesExist_ReturnsEmptyList() {
        // Setup
        when(movieRepository.findAll()).thenReturn(List.of());
        
        // Act
        List<MovieDto> result = movieService.getAllMovies();
        
        // Verify
        assertTrue(result.isEmpty());
    }

    @Test
    void getMovieById_MovieExists_ReturnsMovieDto() {
        // Setup
        UUID movieId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Test Movie").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        
        // Act
        MovieDto result = movieService.getMovieById(movieId);
        
        // Verify
        assertNotNull(result);
        assertEquals(movieId, result.getId());
    }
    
    @Test
    void getMovieById_MovieDoesNotExist_ThrowsMovieNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(MovieNotFoundException.class, () -> movieService.getMovieById(movieId));
    }
    
    @Test
    void createMovie_ValidInput_CreatesMovieAndSendsCreatedEvent() throws Exception {
        // Setup
        UUID genreId = UUID.randomUUID();
        MovieCreateDto movieCreateDto = new MovieCreateDto();
        movieCreateDto.setTitle("New Movie");
        movieCreateDto.setDescription("Awesome movie");
        movieCreateDto.setGenres(List.of(genreId));
        movieCreateDto.setFilmPersonRoles(List.of());
        movieCreateDto.setImages(List.of());
        
        Genre genre = Genre.builder().id(genreId).name("Action").build();
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        
        // Stub mapping & saving (simulate mapper working as identity)
        Movie movie = Movie.builder()
                .id(UUID.randomUUID())
                .title(movieCreateDto.getTitle())
                .description("Awesome movie")
                .genres(List.of(genre))
                .filmPersonRoles(List.of())
                .images(List.of())
                .build();
        when(movieRepository.save(any(Movie.class))).thenReturn(movie);

        var field = movieMapper.getClass().getDeclaredField("genreMapper");
        field.setAccessible(true);
        field.set(movieMapper, genreMapper);
        field.setAccessible(false);

        // Act
        MovieDto result = movieService.createMovie(movieCreateDto);
        
        // Verify
        verify(movieRepository).save(any());
        assertNotNull(result);
        assertEquals(movie.getTitle(), result.getTitle());
        verify(movieEventProducer).sendMovieCreatedEvent(any());
    }
    
    @Test
    void createMovie_InvalidGenre_ThrowsGenreNotFoundException() {
        // Setup
        UUID genreId = UUID.randomUUID();
        MovieCreateDto movieCreateDto = new MovieCreateDto();
        movieCreateDto.setTitle("New Movie");
        movieCreateDto.setDescription("Awesome movie");
        movieCreateDto.setGenres(List.of(genreId));
        movieCreateDto.setFilmPersonRoles(List.of());
        
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(GenreNotFoundException.class, () -> movieService.createMovie(movieCreateDto));
        verify(movieRepository, never()).save(any());
    }
    
    @Test
    void createMovie_FilmPersonNotFound_ThrowsFilmPersonNotFoundException() {
        // Setup
        UUID genreId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        MovieCreateDto movieCreateDto = new MovieCreateDto();
        movieCreateDto.setTitle("New Movie");
        movieCreateDto.setDescription("Awesome movie");
        movieCreateDto.setGenres(List.of(genreId));
        // Simulate a film person role DTO with minimal getters via lambda
        var movieFilmPersonRoleCreateDto = MovieFilmPersonRoleCreateDto.builder()
                        .filmPersonId(filmPersonId)
                        .role(FilmPersonRole.ACTOR)
                        .build();
        movieCreateDto.setFilmPersonRoles(List.of(movieFilmPersonRoleCreateDto));
        
        Genre genre = Genre.builder().id(genreId).name("Action").build();
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(FilmPersonNotFoundException.class, () -> movieService.createMovie(movieCreateDto));
        verify(movieRepository, never()).save(any());
    }
    
    @Test
    void updateMovie_MovieExists_UpdatesMovieAndSendsUpdatedEvent() {
        // Setup
        UUID movieId = UUID.randomUUID();
        Movie existingMovie = Movie.builder().id(movieId).title("Old Title").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(existingMovie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        MovieUpdateDto movieUpdateDto = new MovieUpdateDto();
        movieUpdateDto.setTitle("Updated Title");
        movieUpdateDto.setDescription("Updated description");
        URL url = mock(URL.class);
        movieUpdateDto.setImages(List.of(url));
        
        // Act
        MovieDto result = movieService.updateMovie(movieId, movieUpdateDto);
        
        // Verify
        assertNotNull(result);
        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated description", result.getDescription());
        assertEquals(1, result.getImages().size());
        verify(movieEventProducer).sendMovieUpdatedEvent(any());
    }
    
    @Test
    void updateMovie_MovieNotFound_ThrowsMovieNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        
        MovieUpdateDto movieUpdateDto = new MovieUpdateDto();
        movieUpdateDto.setTitle("Updated Title");
        
        // Act & Assert
        assertThrows(MovieNotFoundException.class, () -> movieService.updateMovie(movieId, movieUpdateDto));
    }
    
    @Test
    void deleteMovie_MovieExists_DeletesMovieAndSendsDeletedEvent() {
        // Setup
        UUID movieId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Delete Movie").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        
        // Act
        movieService.deleteMovie(movieId);
        
        // Verify
        verify(movieRepository).deleteById(movieId);
        verify(movieEventProducer).sendMovieDeletedEvent(any());
    }
    
    @Test
    void deleteMovie_MovieNotFound_ThrowsMovieNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(MovieNotFoundException.class, () -> movieService.deleteMovie(movieId));
        verify(movieRepository, never()).deleteById(any());
    }
    
    @Test
    void assignGenre_MovieAndGenreExist_AssignsGenreAndSendsUpdatedEvent() throws Exception {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        Movie movie = Movie.builder()
                .id(movieId)
                .title("Movie")
                .genres(new ArrayList<>())
                .images(new ArrayList<>())
                .filmPersonRoles(new ArrayList<>())
                .build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        
        Genre genre = Genre.builder().id(genreId).name("Thriller").build();
        when(genreRepository.findById(genreId)).thenReturn(Optional.of(genre));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Field field = movieMapper.getClass().getDeclaredField("genreMapper");
        field.setAccessible(true);
        field.set(movieMapper, genreMapper);

        // Act
        MovieDto result = movieService.assignGenre(movieId, genreId);
        
        // Verify
        assertTrue(result != null && result.getId().equals(movieId));
        verify(movieEventProducer).sendMovieUpdatedEvent(any());
    }
    
    @Test
    void assignGenre_MovieNotFound_ThrowsMovieNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(MovieNotFoundException.class, () -> movieService.assignGenre(movieId, genreId));
    }
    
    @Test
    void assignGenre_GenreNotFound_ThrowsGenreNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Movie").build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(genreRepository.findById(genreId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(GenreNotFoundException.class, () -> movieService.assignGenre(movieId, genreId));
    }
    
    @Test
    void removeGenre_GenreRemovedSuccessfully_SendsUpdatedEvent() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        Genre genre = Genre.builder().id(genreId).name("Comedy").build();
        Movie movie = Movie.builder().id(movieId).title("Movie").genres(List.of(genre)).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        // Act
        MovieDto result = movieService.removeGenre(movieId, genreId);
        
        // Verify
        // Expect the genre to be removed so genres list should be empty
        assertTrue(result.getGenres().isEmpty());
        verify(movieEventProducer).sendMovieUpdatedEvent(any());
    }
    
    @Test
    void removeGenre_MovieNotFound_ThrowsMovieNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        
        // Act & Assert
        assertThrows(MovieNotFoundException.class, () -> movieService.removeGenre(movieId, genreId));
    }
    
    @Test
    void assignFilmPerson_MovieAndFilmPersonExist_AssignsFilmPersonAndSendsUpdatedEvent() throws Exception {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(new ArrayList<>()).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        
        FilmPerson filmPerson = FilmPerson.builder().id(filmPersonId).build();
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));
        
        AssignFilmPersonRoleDto dto = new AssignFilmPersonRoleDto();
        dto.setFilmPersonId(filmPersonId);
        dto.setRole(FilmPersonRole.ACTOR);

        // Act
        MovieDto result = movieService.assignFilmPerson(movieId, dto);
        
        // Verify
        assertTrue(result != null && result.getId().equals(movieId));
        verify(movieFilmPersonRoleProducer).sendMovieFilmPersonRoleAddedEvent(any());
    }
    
    @Test
    void assignFilmPerson_AlreadyAssigned_ThrowsFilmPersonWithThisRoleIsAlreadyAssigned() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        FilmPerson filmPerson = FilmPerson.builder().id(filmPersonId).build();
        FilmPersonInMovieRole existingRole = new FilmPersonInMovieRole(null, filmPerson, FilmPersonRole.ACTOR);
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(List.of(existingRole)).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));
        
        AssignFilmPersonRoleDto dto = new AssignFilmPersonRoleDto();
        dto.setFilmPersonId(filmPersonId);
        dto.setRole(FilmPersonRole.ACTOR);
        
        // Act & Assert
        assertThrows(FilmPersonWithThisRoleIsAlreadyAssigned.class, () -> movieService.assignFilmPerson(movieId, dto));
        verify(movieRepository, never()).save(any());
    }
    
    @Test
    void assignFilmPerson_MovieNotFound_ThrowsMovieNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        
        AssignFilmPersonRoleDto dto = new AssignFilmPersonRoleDto();
        dto.setFilmPersonId(filmPersonId);
        dto.setRole(FilmPersonRole.ACTOR);
        
        // Act & Assert
        assertThrows(MovieNotFoundException.class, () -> movieService.assignFilmPerson(movieId, dto));
    }
    
    @Test
    void assignFilmPerson_FilmPersonNotFound_ThrowsFilmPersonNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(List.of()).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.empty());
        
        AssignFilmPersonRoleDto dto = new AssignFilmPersonRoleDto();
        dto.setFilmPersonId(filmPersonId);
        dto.setRole(FilmPersonRole.ACTOR);
        
        // Act & Assert
        assertThrows(FilmPersonNotFoundException.class, () -> movieService.assignFilmPerson(movieId, dto));
        verify(movieRepository, never()).save(any());
    }
    
    @Test
    void removeFilmPerson_RemovesFilmPersonSuccessfully_SendsUpdatedEvent() throws Exception {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        FilmPerson filmPerson = FilmPerson.builder().id(filmPersonId).build();
        FilmPersonInMovieRole role = new FilmPersonInMovieRole(null, filmPerson, FilmPersonRole.ACTOR);
        Movie movie = Movie.builder().id(movieId).title("Movie").filmPersonRoles(List.of(role)).build();
        when(movieRepository.findById(movieId)).thenReturn(Optional.of(movie));
        when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.<Movie>getArgument(0));
        when(filmPersonRepository.findById(filmPersonId)).thenReturn(Optional.of(filmPerson));

        RemoveFilmPersonRoleDto dto = new RemoveFilmPersonRoleDto();
        dto.setFilmPersonId(filmPersonId);
        dto.setRole(FilmPersonRole.ACTOR);

        // Act
        MovieDto result = movieService.removeFilmPerson(movieId, dto);
        
        // Verify - expect removal so filmPersonRoles list is empty
        assertEquals(1, result.getFilmPersonRoles().size());
        assertEquals(FilmPersonRole.ACTOR, result.getFilmPersonRoles().get(0).getRole());
        verify(movieFilmPersonRoleProducer).sendMovieFilmPersonRoleRemovedEvent(any());
    }
    
    @Test
    void removeFilmPerson_MovieNotFound_ThrowsMovieNotFoundException() {
        // Setup
        UUID movieId = UUID.randomUUID();
        UUID filmPersonId = UUID.randomUUID();
        when(movieRepository.findById(movieId)).thenReturn(Optional.empty());
        
        RemoveFilmPersonRoleDto dto = new RemoveFilmPersonRoleDto();
        dto.setFilmPersonId(filmPersonId);
        dto.setRole(FilmPersonRole.ACTOR);
        
        // Act & Assert
        assertThrows(MovieNotFoundException.class, () -> movieService.removeFilmPerson(movieId, dto));
    }
}
