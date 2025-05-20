package org.movierc.filmpersonservice.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.movierc.events.movie.MovieCreatedEvent;
import org.movierc.events.movie.MovieFilmPersonRole;
import org.movierc.filmpersonservice.dto.FilmPersonCreateDto;
import org.movierc.filmpersonservice.dto.FilmPersonUpdateDto;
import org.movierc.filmpersonservice.entity.FilmPerson;
import org.movierc.filmpersonservice.rabbit.FilmPersonEventProducer;
import org.movierc.filmpersonservice.rabbit.MovieEventConsumer;
import org.movierc.filmpersonservice.repository.FilmPersonRepository;
import org.movierc.shared.enm.FilmPersonRole;
import org.movierc.shared.security.Authorities;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FilmPersonServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmPersonRepository filmPersonRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieEventConsumer movieEventConsumer;

    @MockitoBean
    private RabbitTemplate rabbitTemplate;

    @MockitoBean
    private FilmPersonEventProducer filmPersonEventProducer;

    private FilmPerson filmPerson;

    @BeforeEach
    void setUp() {
        filmPerson = FilmPerson.builder()
                .name("Jane Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .movies(new ArrayList<>())
                .build();
        filmPerson = filmPersonRepository.saveAndFlush(filmPerson);

        UUID movieId = UUID.randomUUID();
        MovieFilmPersonRole role = new MovieFilmPersonRole(movieId, filmPerson.getId(), FilmPersonRole.ACTOR);
        MovieCreatedEvent event = MovieCreatedEvent.builder()
                .id(movieId)
                .title("Test Movie")
                .filmPersonRoles(List.of(role))
                .build();

        movieEventConsumer.handleMovieCreatedEvent(event);
    }

    @Test
    @Transactional
    void createFilmPerson_savesAndSendsEvent() throws Exception {
        var createDto = new FilmPersonCreateDto();
        createDto.setName("John Smith");
        createDto.setDateOfBirth(LocalDate.of(1985, 5, 20));

        mockMvc.perform(post("/api/v1/filmPerson")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_FILM_PERSON))
                        )
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Smith"));

        assertThat(filmPersonRepository.findAll()).hasSize(2);
        verify(filmPersonEventProducer).sendFilmPersonCreatedEvent(any());
    }

    @Test
    @Transactional
    void updateFilmPerson_modifiesAndSendsEvent() throws Exception {
        var updateDto = new FilmPersonUpdateDto();
        updateDto.setName("Janet Doe");
        updateDto.setDateOfBirth(LocalDate.of(1990, 1, 1));

        mockMvc.perform(put("/api/v1/filmPerson/" + filmPerson.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_FILM_PERSON))
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Janet Doe"));

        var updated = filmPersonRepository.findById(filmPerson.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("Janet Doe");
        verify(filmPersonEventProducer).sendFilmPersonUpdatedEvent(any());
    }

    @Test
    @Transactional
    void deleteFilmPerson_removesAndSendsEvent() throws Exception {
        mockMvc.perform(delete("/api/v1/filmPerson/" + filmPerson.getId())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_FILM_PERSON))
                        )
                )
                .andExpect(status().isNoContent());

        assertThat(filmPersonRepository.findById(filmPerson.getId())).isEmpty();
        verify(filmPersonEventProducer).sendFilmPersonDeletedEvent(any());
    }

    @Test
    @Transactional
    void getById_returnsCorrectFilmPerson() throws Exception {
        mockMvc.perform(get("/api/v1/filmPerson/" + filmPerson.getId())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL))
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"));
    }

    @Test
    @Transactional
    void getAll_returnsListOfFilmPersons() throws Exception {
        filmPersonRepository.saveAndFlush(FilmPerson.builder()
                .name("Someone Else")
                .dateOfBirth(LocalDate.of(1975, 7, 7))
                .build());

        mockMvc.perform(get("/api/v1/filmPerson")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL))
                        )
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }
}
