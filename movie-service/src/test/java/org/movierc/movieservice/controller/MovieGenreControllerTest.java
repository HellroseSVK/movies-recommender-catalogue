package org.movierc.movieservice.controller;

import org.junit.jupiter.api.Test;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.dto.MovieDto;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.exception.MovieNotFoundException;
import org.movierc.movieservice.facade.MovieFacade;
import org.movierc.shared.security.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for assigning and removing genres via MovieGenreController.
 */
@WebMvcTest(MovieGenreController.class)
class MovieGenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovieFacade movieFacade;

    @Test
    void assignGenre_ValidInput_ReturnsUpdatedMovieDto() throws Exception {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        MovieDto returnedDto = MovieDto.builder()
                .id(movieId)
                .genres(List.of(GenreDto.builder().id(genreId).name("Thriller").build()))
                .build();
        when(movieFacade.assignGenre(movieId, genreId)).thenReturn(returnedDto);

        mockMvc.perform(put("/api/v1/movie/{movieId}/genre/{genreId}", movieId, genreId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(movieId.toString())))
                .andExpect(jsonPath("$.genres", hasSize(1)))
                .andExpect(jsonPath("$.genres[0].id", is(genreId.toString())));

        verify(movieFacade).assignGenre(movieId, genreId);
    }

    @Test
    void assignGenre_MovieNotFound_Returns404() throws Exception {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieFacade.assignGenre(movieId, genreId))
                .thenThrow(new MovieNotFoundException(movieId));

        mockMvc.perform(put("/api/v1/movie/{movieId}/genre/{genreId}", movieId, genreId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isNotFound());

        verify(movieFacade).assignGenre(movieId, genreId);
    }

    @Test
    void assignGenre_GenreNotFound_Returns404() throws Exception {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieFacade.assignGenre(movieId, genreId))
                .thenThrow(new GenreNotFoundException(genreId));

        mockMvc.perform(put("/api/v1/movie/{movieId}/genre/{genreId}", movieId, genreId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isNotFound());

        verify(movieFacade).assignGenre(movieId, genreId);
    }

    @Test
    void removeGenre_ValidInput_ReturnsUpdatedMovieDto() throws Exception {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        MovieDto returnedDto = MovieDto.builder()
                .id(movieId)
                .genres(List.of())
                .build();
        when(movieFacade.removeGenre(movieId, genreId)).thenReturn(returnedDto);

        mockMvc.perform(delete("/api/v1/movie/{movieId}/genre/{genreId}", movieId, genreId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(movieId.toString())))
                .andExpect(jsonPath("$.genres", hasSize(0)));

        verify(movieFacade).removeGenre(movieId, genreId);
    }

    @Test
    void removeGenre_MovieNotFound_Returns404() throws Exception {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieFacade.removeGenre(movieId, genreId))
                .thenThrow(new MovieNotFoundException(movieId));

        mockMvc.perform(delete("/api/v1/movie/{movieId}/genre/{genreId}", movieId, genreId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isNotFound());

        verify(movieFacade).removeGenre(movieId, genreId);
    }

    @Test
    void removeGenre_GenreNotFound_Returns404() throws Exception {
        UUID movieId = UUID.randomUUID();
        UUID genreId = UUID.randomUUID();
        when(movieFacade.removeGenre(movieId, genreId))
                .thenThrow(new GenreNotFoundException(genreId));

        mockMvc.perform(delete("/api/v1/movie/{movieId}/genre/{genreId}", movieId, genreId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_MOVIE)))
                )
                .andExpect(status().isNotFound());

        verify(movieFacade).removeGenre(movieId, genreId);
    }
}