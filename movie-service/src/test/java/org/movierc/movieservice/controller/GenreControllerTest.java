package org.movierc.movieservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.movierc.movieservice.dto.GenreCreateDto;
import org.movierc.movieservice.dto.GenreDto;
import org.movierc.movieservice.exception.GenreNotFoundException;
import org.movierc.movieservice.facade.GenreFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GenreFacade genreFacade;

    @Test
    void getGenres_GenresExist_ReturnsGenreList() throws Exception {
        // Setup
        GenreDto genreDto = GenreDto.builder()
                .id(UUID.randomUUID())
                .name("Action")
                .build();
        when(genreFacade.getGenres()).thenReturn(List.of(genreDto));

        // Act & Verify
        mockMvc.perform(get("/api/v1/genre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Action")));

        verify(genreFacade).getGenres();
    }

    @Test
    void getGenres_NoGenres_ReturnsEmptyList() throws Exception {
        // Setup
        when(genreFacade.getGenres()).thenReturn(Collections.emptyList());

        // Act & Verify
        mockMvc.perform(get("/api/v1/genre"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(genreFacade).getGenres();
    }

    @Test
    void createGenre_ValidInput_ReturnsCreatedGenreDto() throws Exception {
        // Setup
        GenreCreateDto createDto = GenreCreateDto.builder()
                .name("Comedy")
                .build();
        GenreDto returnedDto = GenreDto.builder()
                .id(UUID.randomUUID())
                .name("Comedy")
                .build();
        when(genreFacade.createGenre(ArgumentMatchers.any(GenreCreateDto.class))).thenReturn(returnedDto);

        // Act & Verify
        mockMvc.perform(post("/api/v1/genre")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Comedy")));

        verify(genreFacade).createGenre(ArgumentMatchers.any(GenreCreateDto.class));
    }

    @Test
    void createGenre_FacadeThrowsError_ReturnsInternalError() {
        // Setup
        GenreCreateDto createDto = GenreCreateDto.builder()
                .name("Drama")
                .build();
        when(genreFacade.createGenre(ArgumentMatchers.any(GenreCreateDto.class))).thenThrow(new RuntimeException("Error"));

        // Act & Verify
        assertThrows(Exception.class, () ->
                mockMvc.perform(post("/api/v1/genre")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(createDto)))
                        .andExpect(status().isInternalServerError()));

        verify(genreFacade).createGenre(ArgumentMatchers.any(GenreCreateDto.class));
    }


    @Test
    void deleteGenre_ValidId_ReturnsNoContent() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();

        // Act & Verify
        mockMvc.perform(delete("/api/v1/genre/{id}", id))
                .andExpect(status().isNoContent());

        verify(genreFacade).deleteGenre(id);
    }

    @Test
    void deleteGenre_GenreNotFound_Returns404() throws Exception {
        // Setup
        UUID id = UUID.randomUUID();
        doThrow(new GenreNotFoundException(id)).when(genreFacade).deleteGenre(id);

        // Act & Verify
        mockMvc.perform(delete("/api/v1/genre/{id}", id))
                .andExpect(status().isNotFound());

        verify(genreFacade).deleteGenre(id);
    }
}
