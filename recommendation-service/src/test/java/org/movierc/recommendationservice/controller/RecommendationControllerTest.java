package org.movierc.recommendationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.exception.CategoryNotFoundException;
import org.movierc.recommendationservice.exception.MovieNotFoundException;
import org.movierc.recommendationservice.facade.RecommendationFacade;
import org.movierc.shared.security.Authorities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void getRecommendationsByCategoriesAboveThreshold_HasAboveThreshold_ReturnsRecommendationList() throws Exception {
        // Setup
        UUID categoryId = UUID.randomUUID();
        MovieDto dto = MovieDto.builder()
                .id(UUID.randomUUID())
                .title("The Matrix")
                .userAverageRatings(Collections.emptyList())
                .build();
        when(recommendationFacade.getRecommendationsByCategoriesAboveThreshold(Set.of(categoryId), 0)).thenReturn(List.of(dto));

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation/by-categories/0")
                        .param("categoryIds", categoryId.toString())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Matrix")));

        verify(recommendationFacade).getRecommendationsByCategoriesAboveThreshold(Set.of(categoryId), 0);
    }

    @Test
    void getRecommendationsByMovieId_ExistsWithId_ReturnsRecommendationList() throws Exception {
        // Setup
        UUID movieId = UUID.randomUUID();
        MovieDto dto = MovieDto.builder()
                .id(UUID.randomUUID())
                .title("The Matrix")
                .userAverageRatings(Collections.emptyList())
                .build();
        when(recommendationFacade.getRecommendationsByMovieId(movieId)).thenReturn(List.of(dto));

        // Act & Verify
        mockMvc.perform(get("/api/v1/recommendation/by-movie/{movieId}", movieId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("The Matrix")));

        verify(recommendationFacade).getRecommendationsByMovieId(movieId);
    }

    @Test
    void getRecommendationsByCategoriesAboveThreshold_CategoryNotFound_ReturnsNotFound() throws Exception {
        UUID categoryId = UUID.randomUUID();
        when(recommendationFacade.getRecommendationsByCategoriesAboveThreshold(Set.of(categoryId), 0))
                .thenThrow(new CategoryNotFoundException(categoryId));
        mockMvc.perform(get("/api/v1/recommendation/by-categories/0")
                        .param("categoryIds", categoryId.toString())
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Category Not Found Error")))
                .andExpect(jsonPath("$.detail", is("Category not found with id: " + categoryId)));
    }

    @Test
    void getRecommendationsByMovieId_MovieNotFound_ReturnsNotFound() throws Exception {
        UUID movieId = UUID.randomUUID();
        when(recommendationFacade.getRecommendationsByMovieId(movieId))
                .thenThrow(new MovieNotFoundException(movieId));
        mockMvc.perform(get("/api/v1/recommendation/by-movie/{movieId}", movieId)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title", is("Movie Not Found Error")))
                .andExpect(jsonPath("$.detail", is("Movie not found with id: " + movieId)));
    }
}