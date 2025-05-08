package org.movierc.feedbackservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.movierc.feedbackservice.dto.RatingCategoryCreateDto;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.facade.RatingCategoryFacade;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RatingCategoryController.class)
class RatingCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    @MockitoBean
    private RatingCategoryFacade ratingCategoryFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllRatingCategories_ReturnsList() throws Exception {
        RatingCategoryDto dto = RatingCategoryDto.builder()
                .id(UUID.randomUUID())
                .name("Story")
                .build();

        when(ratingCategoryFacade.getAllRatingCategories()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/rating/category")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Story")));

        verify(ratingCategoryFacade).getAllRatingCategories();
    }

    @Test
    void getRatingCategoryById_ReturnsCategory() throws Exception {
        UUID id = UUID.randomUUID();
        RatingCategoryDto dto = RatingCategoryDto.builder()
                .id(id)
                .name("Visuals")
                .build();

        when(ratingCategoryFacade.getRatingCategoryById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/rating/category/{id}", id)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.READ_ALL)))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(id.toString())))
                .andExpect(jsonPath("$.name", is("Visuals")));

        verify(ratingCategoryFacade).getRatingCategoryById(id);
    }

    @Test
    void createRatingCategory_ReturnsCreatedCategory() throws Exception {
        RatingCategoryCreateDto createDto = RatingCategoryCreateDto.builder()
                .name("Performance")
                .build();

        RatingCategoryDto responseDto = RatingCategoryDto.builder()
                .id(UUID.randomUUID())
                .name("Performance")
                .build();

        when(ratingCategoryFacade.createRatingCategory(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/v1/rating/category")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto))
                        .with(SecurityMockMvcRequestPostProcessors.jwt()
                                .authorities(new SimpleGrantedAuthority(Authorities.WRITE_SCOPE_RATING)))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is("Performance")));

        verify(ratingCategoryFacade).createRatingCategory(createDto);
    }
}
