package org.movierc.feedbackservice.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.movierc.feedbackservice.dto.RatingCategoryCreateDto;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.service.RatingCategoryService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingCategoryFacadeTest {

    @Mock
    private RatingCategoryService ratingCategoryService;

    private RatingCategoryFacade ratingCategoryFacade;

    @BeforeEach
    void setUp() {
        ratingCategoryFacade = new RatingCategoryFacade(ratingCategoryService);
    }

    @Test
    void getAllRatingCategories_ReturnsList() {
        RatingCategoryDto dto = RatingCategoryDto.builder()
            .id(UUID.randomUUID())
            .name("Performance")
            .build();

        when(ratingCategoryService.getAllRatingCategories()).thenReturn(List.of(dto));

        List<RatingCategoryDto> result = ratingCategoryFacade.getAllRatingCategories();

        assertEquals(1, result.size());
        verify(ratingCategoryService).getAllRatingCategories();
    }

    @Test
    void getRatingCategoryById_ReturnsDto() {
        UUID id = UUID.randomUUID();
        RatingCategoryDto dto = RatingCategoryDto.builder()
            .id(id)
            .name("Sound")
            .build();

        when(ratingCategoryService.getRatingCategoryById(id)).thenReturn(dto);

        RatingCategoryDto result = ratingCategoryFacade.getRatingCategoryById(id);

        assertEquals(id, result.getId());
        verify(ratingCategoryService).getRatingCategoryById(id);
    }

    @Test
    void createRatingCategory_ReturnsCreatedDto() {
        RatingCategoryCreateDto createDto = RatingCategoryCreateDto.builder()
            .name("Visuals")
            .build();

        RatingCategoryDto createdDto = RatingCategoryDto.builder()
            .id(UUID.randomUUID())
            .name("Visuals")
            .build();

        when(ratingCategoryService.createRatingCategory(createDto)).thenReturn(createdDto);

        RatingCategoryDto result = ratingCategoryFacade.createRatingCategory(createDto);

        assertEquals("Visuals", result.getName());
        verify(ratingCategoryService).createRatingCategory(createDto);
    }
}
