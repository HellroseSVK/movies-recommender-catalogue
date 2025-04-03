package org.movierc.movieservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.movierc.movieservice.dto.*;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.facade.MovieFacade;
import org.movierc.shared.enm.FilmPersonRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MovieController.class)
class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MovieFacade movieFacade;

    @Test
    void getAllMovies_MoviesExist_ReturnsMovieList() throws Exception {
        // Setup
        MovieDto movieDto = MovieDto.builder()
                .id(UUID.randomUUID())
                .title("Test Movie")
                .build();
        when(movieFacade.getAllMovies()).thenReturn(List.of(movieDto));

        // Act & Verify
        mockMvc.perform(get("/api/v1/movie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Movie")));

        verify(movieFacade).getAllMovies();
    }

    @Test
    void getAllMovies_NoMovies_ReturnsEmptyList() throws Exception {
        // Setup
        when(movieFacade.getAllMovies()).thenReturn(Collections.emptyList());

        // Act & Verify
        mockMvc.perform(get("/api/v1/movie"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(movieFacade).getAllMovies();
    }

    @Test
    void getMovieById_MovieFound_ReturnsMovieDto() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        MovieDto movieDto = MovieDto.builder()
                .id(id)
                .title("Test Movie")
                .build();
        when(movieFacade.getMovieById(id)).thenReturn(movieDto);

        // Act & Verify
        mockMvc.perform(get("/api/v1/movie/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.title", is("Test Movie")));

        verify(movieFacade).getMovieById(id);
    }

    @Test
    void getMovieById_MovieNotFound_Returns404() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        when(movieFacade.getMovieById(id)).thenThrow(new MovieNotFoundException(id));

        // Act & Verify
        mockMvc.perform(get("/api/v1/movie/{id}", id))
                .andExpect(status().isNotFound());

        verify(movieFacade).getMovieById(id);
    }

    @Test
    void createMovie_ValidInput_ReturnsCreatedMovieDto() throws Exception {
        // Setup
        MovieCreateDto createDto = MovieCreateDto.builder()
            .title("New Movie")
            .description("A new movie description")
            .images(List.of(new URL("http://example.com/image1.jpg"), new URL("http://example.com/image2.jpg")))
            .genres(List.of(UUID.randomUUID()))
            .filmPersonRoles(List.of(MovieFilmPersonRoleCreateDto.builder()
                .filmPersonId(UUID.randomUUID())
                .role(FilmPersonRole.ACTOR)
                .build()))
            .build();
        MovieDto returnedDto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("New Movie")
            .description("A new movie description")
            .images(List.of(new URL("http://example.com/image1.jpg"), new URL("http://example.com/image2.jpg")))
            .genres(List.of(GenreDto.builder().id(UUID.randomUUID()).name("Action").build()))
            .filmPersonRoles(List.of(MovieFilmPersonRoleDto.builder()
                .filmPerson(FilmPersonDto.builder().id(UUID.randomUUID()).name("John Doe").build())
                .role(FilmPersonRole.ACTOR)
                .build()))
            .build();
        when(movieFacade.createMovie(ArgumentMatchers.any(MovieCreateDto.class))).thenReturn(returnedDto);

        // Act & Verify
        mockMvc.perform(post("/api/v1/movie")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title", is("New Movie")));

        verify(movieFacade).createMovie(ArgumentMatchers.any(MovieCreateDto.class));
    }

    @Test
    void createMovie_MissingRequiredField_ReturnsBadRequest() throws Exception {
        // Setup: omit mandatory "title"
        MovieCreateDto createDto = MovieCreateDto.builder().build();

        // Act & Verify
        mockMvc.perform(post("/api/v1/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateMovie_ValidInput_ReturnsUpdatedMovieDto() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        MovieUpdateDto updateDto = MovieUpdateDto.builder()
            .title("Updated Movie")
            .description("An updated movie description")
            .images(List.of(new URL("http://example.com/image1.jpg"), new URL("http://example.com/image2.jpg")))
            .build();
        MovieDto updatedDto = MovieDto.builder()
            .id(id)
            .title("Updated Movie")
            .description("An updated movie description")
            .images(List.of(new URL("http://example.com/image1.jpg"), new URL("http://example.com/image2.jpg")))
            .genres(List.of(GenreDto.builder().id(UUID.randomUUID()).name("Action").build()))
            .filmPersonRoles(List.of(MovieFilmPersonRoleDto.builder()
                .filmPerson(FilmPersonDto.builder().id(UUID.randomUUID()).name("John Doe").build())
                .role(FilmPersonRole.ACTOR)
                .build()))
            .build();
        when(movieFacade.updateMovie(id, updateDto)).thenReturn(updatedDto);

        // Act & Verify
        mockMvc.perform(put("/api/v1/movie/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title", is("Updated Movie")));

        verify(movieFacade).updateMovie(id, updateDto);
    }

    @Test
    void updateMovie_MovieNotFound_Returns404() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        MovieUpdateDto updateDto = MovieUpdateDto.builder()
            .title("Non existent")
            .description("A non existent movie description")
            .images(List.of(new URL("http://example.com/image1.jpg"), new URL("http://example.com/image2.jpg")))
            .build();
        when(movieFacade.updateMovie(id, updateDto))
            .thenThrow(new MovieNotFoundException(id));

        // Act & Verify
        mockMvc.perform(put("/api/v1/movie/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isNotFound());

        verify(movieFacade).updateMovie(id, updateDto);
    }

    @Test
    void deleteMovie_ValidId_ReturnsNoContent() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();

        // Act & Verify
        mockMvc.perform(delete("/api/v1/movie/{id}", id))
                .andExpect(status().isNoContent());

        verify(movieFacade).deleteMovie(id);
    }

    @Test
    void deleteMovie_MovieNotFound_Returns404() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        doThrow(new MovieNotFoundException(id)).when(movieFacade).deleteMovie(id);

        // Act & Verify
        mockMvc.perform(delete("/api/v1/movie/{id}", id))
                .andExpect(status().isNotFound());

        verify(movieFacade).deleteMovie(id);
    }
}
