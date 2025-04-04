package org.movierc.feedbackservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.movierc.feedbackservice.dto.RatingCategoryCreateDto;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.entity.RatingCategory;
import org.movierc.feedbackservice.exception.RatingCategoryNotFoundException;
import org.movierc.feedbackservice.mapper.RatingCategoryCreateDtoMapper;
import org.movierc.feedbackservice.mapper.RatingCategoryMapper;
import org.movierc.feedbackservice.rabbit.RatingCategoryEventProducer;
import org.movierc.feedbackservice.repository.RatingCategoryRepository;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingCategoryServiceTest {

    @Mock
    private RatingCategoryRepository ratingCategoryRepository;

    @Mock
    private RatingCategoryEventProducer ratingCategoryEventProducer;

    private final RatingCategoryCreateDtoMapper ratingCategoryCreateDtoMapper = Mappers.getMapper(RatingCategoryCreateDtoMapper.class);
    private final RatingCategoryMapper ratingCategoryMapper = Mappers.getMapper(RatingCategoryMapper.class);

    private RatingCategoryService ratingCategoryService;

    @BeforeEach
    void setUp() {
        ratingCategoryService = new RatingCategoryService(
            ratingCategoryRepository,
            ratingCategoryMapper,
            ratingCategoryCreateDtoMapper,
            ratingCategoryEventProducer
        );
    }

    @Test
    void getAllRatingCategories_WhenPresent_ReturnsList() {
        RatingCategory entity = RatingCategory.builder()
            .id(UUID.randomUUID())
            .name("Acting")
            .build();

        when(ratingCategoryRepository.findAll()).thenReturn(List.of(entity));

        List<RatingCategoryDto> result = ratingCategoryService.getAllRatingCategories();

        assertEquals(1, result.size());
        assertEquals("Acting", result.getFirst().getName());
        verify(ratingCategoryRepository, times(1)).findAll();
    }

    @Test
    void getRatingCategoryById_WhenExists_ReturnsDto() {
        UUID id = UUID.randomUUID();
        RatingCategory entity = RatingCategory.builder()
            .id(id)
            .name("Storyline")
            .build();

        when(ratingCategoryRepository.findById(id)).thenReturn(Optional.of(entity));

        RatingCategoryDto result = ratingCategoryService.getRatingCategoryById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Storyline", result.getName());
        verify(ratingCategoryRepository, times(1)).findById(id);
    }

    @Test
    void getRatingCategoryById_WhenNotExists_ThrowsException() {
        UUID id = UUID.randomUUID();
        when(ratingCategoryRepository.findById(id)).thenReturn(Optional.empty());

        RatingCategoryNotFoundException exception = assertThrows(
            RatingCategoryNotFoundException.class,
            () -> ratingCategoryService.getRatingCategoryById(id)
        );

        assertTrue(exception.getMessage().contains(id.toString()));
        verify(ratingCategoryRepository, times(1)).findById(id);
    }

    @Test
    void createRatingCategory_ValidInput_ReturnsDto() {
        RatingCategoryCreateDto createDto = RatingCategoryCreateDto.builder()
            .name("Visuals")
            .build();

        RatingCategory entity = ratingCategoryCreateDtoMapper.ratingCategoryCreateDtoToRatingCategory(createDto);
        entity.setId(UUID.randomUUID());

        when(ratingCategoryRepository.save(any(RatingCategory.class))).thenReturn(entity);

        RatingCategoryDto result = ratingCategoryService.createRatingCategory(createDto);

        assertNotNull(result);
        assertEquals("Visuals", result.getName());
        verify(ratingCategoryRepository, times(1)).save(any(RatingCategory.class));
        verify(ratingCategoryEventProducer, times(1)).sendRatingCategoryCreatedEvent(any());
    }
}
