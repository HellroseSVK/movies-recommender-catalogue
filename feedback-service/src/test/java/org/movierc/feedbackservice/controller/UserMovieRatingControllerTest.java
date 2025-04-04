package org.movierc.feedbackservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.facade.UserMovieRatingFacade;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserMovieRatingController.class)
class UserMovieRatingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockitoBean
    private UserMovieRatingFacade userMovieRatingFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllRatings_ReturnsList() throws Exception {
        UserMovieRatingDto dto = new UserMovieRatingDto();
        when(userMovieRatingFacade.getAllRatings()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/rating"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        verify(userMovieRatingFacade).getAllRatings();
    }

    @Test
    void getRatingsByUserId_ReturnsList() throws Exception {
        UUID userId = UUID.randomUUID();
        when(userMovieRatingFacade.getRatingsByUserId(userId)).thenReturn(List.of(new UserMovieRatingDto()));

        mockMvc.perform(get("/api/v1/rating/user/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        verify(userMovieRatingFacade).getRatingsByUserId(userId);
    }

    @Test
    void getRatingsByMovieId_ReturnsList() throws Exception {
        UUID movieId = UUID.randomUUID();
        when(userMovieRatingFacade.getRatingsByMovieId(movieId)).thenReturn(List.of(new UserMovieRatingDto()));

        mockMvc.perform(get("/api/v1/rating/movie/{movieId}", movieId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)));

        verify(userMovieRatingFacade).getRatingsByMovieId(movieId);
    }

    @Test
    void getRatingById_ReturnsRating() throws Exception {
        UUID id = UUID.randomUUID();
        UserMovieRatingDto dto = new UserMovieRatingDto();
        dto.setId(id);
        when(userMovieRatingFacade.getRatingById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/rating/{id}", id))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(id.toString())));

        verify(userMovieRatingFacade).getRatingById(id);
    }

    @Test
    void createRating_ReturnsCreatedRating() throws Exception {
        UserMovieRatingCreateDto createDto = UserMovieRatingCreateDto.builder()
            .userId(UUID.randomUUID())
            .movieId(UUID.randomUUID())
            .ratingDetails(Collections.emptyList())
            .comment("Awesome!")
            .build();

        UserMovieRatingDto responseDto = new UserMovieRatingDto();
        responseDto.setId(UUID.randomUUID());

        when(userMovieRatingFacade.createRating(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/rating")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(responseDto.getId().toString())));

        verify(userMovieRatingFacade).createRating(createDto);
    }

    @Test
    void updateRating_ReturnsUpdatedRating() throws Exception {
        UUID id = UUID.randomUUID();
        UserMovieRatingUpdateDto updateDto = UserMovieRatingUpdateDto.builder()
            .userId(UUID.randomUUID())
            .movieId(UUID.randomUUID())
            .ratingDetails(Collections.emptyList())
            .comment("Updated")
            .build();

        UserMovieRatingDto updated = new UserMovieRatingDto();
        updated.setId(id);

        when(userMovieRatingFacade.updateRating(id, updateDto)).thenReturn(updated);

        mockMvc.perform(put("/api/v1/rating/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(id.toString())));

        verify(userMovieRatingFacade).updateRating(id, updateDto);
    }

    @Test
    void deleteRating_ReturnsNoContent() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/rating/{id}", id))
            .andExpect(status().isNoContent());

        verify(userMovieRatingFacade).deleteRating(id);
    }
}
