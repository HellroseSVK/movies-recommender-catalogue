package org.movierc.movieservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.movierc.events.filmPerson.FilmPersonCreatedEvent;
import org.movierc.movieservice.dto.MovieCreateDto;
import org.movierc.movieservice.dto.MovieFilmPersonRoleCreateDto;
import org.movierc.movieservice.dto.MovieUpdateDto;
import org.movierc.movieservice.entity.FilmPerson;
import org.movierc.movieservice.entity.FilmPersonInMovieRole;
import org.movierc.movieservice.entity.Movie;
import org.movierc.movieservice.rabbit.FilmPersonEventConsumer;
import org.movierc.movieservice.rabbit.MovieEventProducer;
import org.movierc.movieservice.repository.MovieRepository;
import org.movierc.shared.enm.FilmPersonRole;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("without-artemis")
class MovieServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FilmPersonEventConsumer filmPersonEventConsumer;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private MovieRepository movieRepository;

    @MockitoBean
    private MovieEventProducer userMovieRatingEventProducer;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    public SecurityFilterChain securityFilterChain;

    private FilmPerson filmPerson;
    private Movie movie;
    @Autowired
    private MovieEventProducer movieEventProducer;

    @BeforeEach
    void setUp() {
        filmPerson = FilmPerson.builder()
                .id(UUID.randomUUID())
                .name("John Doe")
                .build();

        var filmPersonCreatedEvent = FilmPersonCreatedEvent.builder()
                .id(filmPerson.getId())
                .name(filmPerson.getName())
                .build();


        filmPersonEventConsumer.handleFilmPersonCreatedEvent(filmPersonCreatedEvent);
        entityManager.flush();
    }

    @Test
    @Transactional
    void createMovie_createsSuccessfully() throws Exception {
        var request = MovieCreateDto.builder()
                .title("Test Movie")
                .description("Test Description")
                .genres(new ArrayList<>())
                .filmPersonRoles(new ArrayList<>(List.of(
                        new MovieFilmPersonRoleCreateDto(filmPerson.getId(), FilmPersonRole.ACTOR)
                )))
                .images(new ArrayList<>())
                .build();

        mockMvc.perform(post("/api/v1/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        var createdMovie = movieRepository.findAll().getFirst();
        assertEquals(request.getTitle(), createdMovie.getTitle());
        assertEquals(request.getDescription(), createdMovie.getDescription());
        assertEquals(request.getFilmPersonRoles().get(0).getFilmPersonId(), createdMovie.getFilmPersonRoles().get(0).getFilmPerson().getId());
        verify(movieEventProducer).sendMovieCreatedEvent(any());
    }

    @Test
    @Transactional
    void getMovie_returnsMovie() throws Exception {
        movie = Movie.builder()
                .title("Test Movie")
                .description("Test Description")
                .filmPersonRoles(new ArrayList<>(List.of(
                        FilmPersonInMovieRole.builder()
                                .filmPerson(filmPerson)
                                .role(FilmPersonRole.ACTOR)
                                .build()
                )))
                .genres(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        var savedMovie = movieRepository.saveAndFlush(movie);

        mockMvc.perform(get("/api/v1/movie/" + savedMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedMovie.getId().toString()))
                .andExpect(jsonPath("$.title").value(savedMovie.getTitle()))
                .andExpect(jsonPath("$.description").value(savedMovie.getDescription()));
    }

    @Test
    @Transactional
    void updateMovie_updatesSuccessfully() throws Exception {
        movie = Movie.builder()
                .title("Test Movie")
                .description("Test Description")
                .filmPersonRoles(new ArrayList<>(List.of(
                        FilmPersonInMovieRole.builder()
                                .filmPerson(filmPerson)
                                .role(FilmPersonRole.ACTOR)
                                .build()
                )))
                .genres(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        var savedMovie = movieRepository.saveAndFlush(movie);

        var updateRequest = MovieUpdateDto.builder()
                .title("Updated Movie")
                .description("Updated Description")
                .images(new ArrayList<>())
                .build();

        mockMvc.perform(put("/api/v1/movie/" + savedMovie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        var updatedMovie = movieRepository.findById(savedMovie.getId()).orElseThrow();
        assertEquals(updateRequest.getTitle(), updatedMovie.getTitle());
        assertEquals(updateRequest.getDescription(), updatedMovie.getDescription());
    }

    @Test
    @Transactional
    void deleteMovie_deletesSuccessfully() throws Exception {
        movie = Movie.builder()
                .title("Test Movie")
                .description("Test Description")
                .filmPersonRoles(new ArrayList<>(List.of(
                        FilmPersonInMovieRole.builder()
                                .filmPerson(filmPerson)
                                .role(FilmPersonRole.ACTOR)
                                .build()
                )))
                .genres(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        var savedMovie = movieRepository.saveAndFlush(movie);

        mockMvc.perform(delete("/api/v1/movie/" + savedMovie.getId()))
                .andExpect(status().isNoContent());

        assertTrue(movieRepository.findById(savedMovie.getId()).isEmpty());
    }

    @Test
    @Transactional
    void getAllMovies_returnsAllMovies() throws Exception {
        movie = Movie.builder()
                .title("Test Movie")
                .description("Test Description")
                .filmPersonRoles(new ArrayList<>(List.of(
                        FilmPersonInMovieRole.builder()
                                .filmPerson(filmPerson)
                                .role(FilmPersonRole.ACTOR)
                                .build()
                )))
                .genres(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        var movie2 = Movie.builder()
                .title("Test Movie 2")
                .description("Test Description 2")
                .filmPersonRoles(new ArrayList<>(List.of(
                        FilmPersonInMovieRole.builder()
                                .filmPerson(filmPerson)
                                .role(FilmPersonRole.ACTOR)
                                .build()
                )))
                .genres(new ArrayList<>())
                .images(new ArrayList<>())
                .build();

        movieRepository.saveAndFlush(movie);
        movieRepository.saveAndFlush(movie2);

        mockMvc.perform(get("/api/v1/movie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
