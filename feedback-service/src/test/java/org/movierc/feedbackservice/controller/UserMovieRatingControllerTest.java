package org.movierc.feedbackservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movierc.feedbackservice.dto.UserMovieRatingCreateDto;
import org.movierc.feedbackservice.dto.UserMovieRatingDto;
import org.movierc.feedbackservice.dto.UserMovieRatingUpdateDto;
import org.movierc.feedbackservice.facade.UserMovieRatingFacade;
import org.movierc.shared.security.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
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

        mockMvc.perform(get("/api/v1/rating")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(userMovieRatingFacade).getAllRatings();
    }

    @Test
    void getRatingsByUserId_ReturnsList() throws Exception {
        var userId = UUID.randomUUID().toString();
        when(userMovieRatingFacade.getRatingsByUserId(userId)).thenReturn(List.of(new UserMovieRatingDto()));

        mockMvc.perform(get("/api/v1/rating/user/{userId}", userId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        verify(userMovieRatingFacade).getRatingsByUserId(userId);
    }

    @Test
    void getRatingsByMovieId_ReturnsList() throws Exception {
        UUID movieId = UUID.randomUUID();
        when(userMovieRatingFacade.getRatingsByMovieId(movieId)).thenReturn(List.of(new UserMovieRatingDto()));

        mockMvc.perform(get("/api/v1/rating/movie/{movieId}", movieId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
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

        mockMvc.perform(get("/api/v1/rating/{id}", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));

        verify(userMovieRatingFacade).getRatingById(id);
    }

    @Test
    void createRating_ReturnsCreatedRating() throws Exception {
        var userId = "test-user";
        UserMovieRatingCreateDto createDto = UserMovieRatingCreateDto.builder()
                .userId(userId)
                .movieId(UUID.randomUUID())
                .ratingDetails(Collections.emptyList())
                .comment("Awesome!")
                .build();

        UserMovieRatingDto responseDto = new UserMovieRatingDto();
        responseDto.setId(UUID.randomUUID());

        when(userMovieRatingFacade.createRating(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(b -> b.subject(userId))
                                .authorities(new SimpleGrantedAuthority(Authorities.RATING_SCOPE)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.getId().toString())));

        verify(userMovieRatingFacade).createRating(createDto);
    }

    @Test
    void createRating_ReturnsCreatedRatingAsAdmin() throws Exception {
        var userId = "test-user";
        UserMovieRatingCreateDto createDto = UserMovieRatingCreateDto.builder()
                .userId(userId)
                .movieId(UUID.randomUUID())
                .ratingDetails(Collections.emptyList())
                .comment("Awesome!")
                .build();

        UserMovieRatingDto responseDto = new UserMovieRatingDto();
        responseDto.setId(UUID.randomUUID());

        when(userMovieRatingFacade.createRating(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/rating")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_ALL_ADMIN)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(responseDto.getId().toString())));

        verify(userMovieRatingFacade).createRating(createDto);
    }

    @Test
    void updateRating_ReturnsUpdatedRating() throws Exception {
        var userId = "test-user";
        UUID id = UUID.randomUUID();
        UserMovieRatingUpdateDto updateDto = UserMovieRatingUpdateDto.builder()
                .ratingDetails(Collections.emptyList())
                .comment("Updated")
                .build();

        UserMovieRatingDto updated = new UserMovieRatingDto();
        updated.setId(id);

        when(userMovieRatingFacade.updateRating(id, updateDto, userId)).thenReturn(updated);

        mockMvc.perform(put("/api/v1/rating/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(b -> b.subject(userId))
                                .authorities(new SimpleGrantedAuthority(Authorities.RATING_SCOPE)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));

        verify(userMovieRatingFacade).updateRating(id, updateDto, userId);
    }

    @Test
    void updateRating_ReturnsUpdatedRatingAsAdmin() throws Exception {
        var userId = "test-user";
        UUID id = UUID.randomUUID();
        UserMovieRatingUpdateDto updateDto = UserMovieRatingUpdateDto.builder()
                .ratingDetails(Collections.emptyList())
                .comment("Updated")
                .build();

        UserMovieRatingDto updated = new UserMovieRatingDto();
        updated.setId(id);

        when(userMovieRatingFacade.updateRating(id, updateDto, null)).thenReturn(updated);

        mockMvc.perform(put("/api/v1/rating/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_ALL_ADMIN)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())));

        verify(userMovieRatingFacade).updateRating(id, updateDto, null);
    }

    @Test
    void deleteRating_ReturnsNoContent() throws Exception {
        var userId = "test-user";
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/rating/{id}", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .jwt(b -> b.subject(userId))
                                .authorities(new SimpleGrantedAuthority(Authorities.RATING_SCOPE)))
                )
                .andExpect(status().isNoContent());

        verify(userMovieRatingFacade).deleteRating(id, userId);
    }

    @Test
    void deleteRating_ReturnsNoContentAsAdmin() throws Exception {
        var userId = "test-user";
        UUID id = UUID.randomUUID();

        mockMvc.perform(delete("/api/v1/rating/{id}", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_ALL_ADMIN)))
                )
                .andExpect(status().isNoContent());

        verify(userMovieRatingFacade).deleteRating(id, null);
    }
}
