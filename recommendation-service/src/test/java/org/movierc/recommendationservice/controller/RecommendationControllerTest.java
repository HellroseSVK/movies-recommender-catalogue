package org.movierc.recommendationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movierc.recommendationservice.dto.GetRecommendationByCategoriesWeightedDto;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.facade.RecommendationFacade;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecommendationController.class)
class RecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockitoBean
    private RecommendationFacade recommendationFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getRecommendations_ReturnsRecommendationList() throws Exception {
        // Setup
        MovieDto dto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("The Matrix")
            .userAverageRatings(Collections.emptyList())
            .build();
        when(recommendationFacade.getRecommendations()).thenReturn(List.of(dto));
        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title", is("The Matrix")));

        verify(recommendationFacade).getRecommendations();
    }

    @Test
    void getRecommendations_NoRecommendations_ReturnsEmptyList() throws Exception {
        // Setup
        when(recommendationFacade.getRecommendations()).thenReturn(Collections.emptyList());

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));

        verify(recommendationFacade).getRecommendations();
    }

    @Test
    void getRecommendationsByCategoriesAboveThreshold_ReturnsRecommendationList() throws Exception {
        // Setup
        UUID categoryId = UUID.randomUUID();
        MovieDto dto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("The Matrix")
            .userAverageRatings(Collections.emptyList())
            .build();
        when(recommendationFacade.getRecommendationsByCategoriesAboveThreshold(List.of(categoryId), 0)).thenReturn(List.of(dto));

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation/by-categories/0")
                .param("categoryIds", categoryId.toString()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title", is("The Matrix")));

        verify(recommendationFacade).getRecommendationsByCategoriesAboveThreshold(List.of(categoryId), 0);
    }

    @Test
    void getRecommendationsByCategoriesWeighted_ReturnsRecommendationList() throws Exception {
        // Setup
        GetRecommendationByCategoriesWeightedDto weightedDto = GetRecommendationByCategoriesWeightedDto.builder()
            .categoryId(UUID.randomUUID())
            .weight(0.5)
            .build();
        MovieDto dto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("The Matrix")
            .userAverageRatings(Collections.emptyList())
            .build();
        when(recommendationFacade.getRecommendationsByCategoriesWeighted(List.of(weightedDto))).thenReturn(List.of(dto));

        // Act & Verify
        mockMvc.perform(post("/api/v1/recommendation/by-categories/weighted")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(List.of(weightedDto))))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title", is("The Matrix")));

        verify(recommendationFacade).getRecommendationsByCategoriesWeighted(List.of(weightedDto));
    }

    @Test
    void getRecommendationsByRated_ReturnsRecommendationList() throws Exception {
        // Setup
        UUID userId = UUID.randomUUID();
        MovieDto dto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("The Matrix")
            .userAverageRatings(Collections.emptyList())
            .build();
        when(recommendationFacade.getRecommendationsByRated(userId)).thenReturn(List.of(dto));

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation/by-rated/{userId}", userId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title", is("The Matrix")));

        verify(recommendationFacade).getRecommendationsByRated(userId);
    }

    @Test
    void getRecommendationsByMovieId_ReturnsRecommendationList() throws Exception {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieDto dto = MovieDto.builder()
            .id(UUID.randomUUID())
            .title("The Matrix")
            .userAverageRatings(Collections.emptyList())
            .build();
        when(recommendationFacade.getRecommendationsByMovieId(movieId)).thenReturn(List.of(dto));

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation/by-movie/{movieId}", movieId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].title", is("The Matrix")));

        verify(recommendationFacade).getRecommendationsByMovieId(movieId);
    }
}