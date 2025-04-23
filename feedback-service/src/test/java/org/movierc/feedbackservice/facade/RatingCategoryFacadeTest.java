package org.movierc.feedbackservice.facade;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.movierc.feedbackservice.dto.RatingCategoryCreateDto;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.entity.RatingCategory;
import org.movierc.feedbackservice.mapper.RatingCategoryCreateDtoMapper;
import org.movierc.feedbackservice.mapper.RatingCategoryMapper;
import org.movierc.feedbackservice.rabbit.RatingCategoryEventProducer;
import org.movierc.feedbackservice.service.RatingCategoryService;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingCategoryFacadeTest {

    @Mock
    private RatingCategoryService ratingCategoryService;

    @Mock
    private RatingCategoryEventProducer ratingCategoryEventProducer;

    private RatingCategoryFacade ratingCategoryFacade;

    private final RatingCategoryMapper ratingCategoryMapper = Mappers.getMapper(RatingCategoryMapper.class);
    private final RatingCategoryCreateDtoMapper ratingCategoryCreateDtoMapper = Mappers.getMapper(RatingCategoryCreateDtoMapper.class);

    @BeforeEach
    void setUp() {
        ratingCategoryFacade = new RatingCategoryFacade(
            ratingCategoryService,
            ratingCategoryEventProducer,
            ratingCategoryMapper,
            ratingCategoryCreateDtoMapper
        );
    }

    @Test
    void getAllRatingCategories_ReturnsMappedList() {
        RatingCategory entity = RatingCategory.builder()
            .id(UUID.randomUUID())
            .name("Performance")
            .build();

        when(ratingCategoryService.getAllRatingCategories()).thenReturn(List.of(entity));

        List<RatingCategoryDto> result = ratingCategoryFacade.getAllRatingCategories();

        assertEquals(1, result.size());
        assertEquals("Performance", result.get(0).getName());
        verify(ratingCategoryService).getAllRatingCategories();
    }

    @Test
    void getRatingCategoryById_ReturnsMappedDto() {
        UUID id = UUID.randomUUID();
        RatingCategory entity = RatingCategory.builder()
            .id(id)
            .name("Sound")
            .build();

        when(ratingCategoryService.getRatingCategoryById(id)).thenReturn(entity);

        RatingCategoryDto result = ratingCategoryFacade.getRatingCategoryById(id);

        assertNotNull(result);
        assertEquals("Sound", result.getName());
        assertEquals(id, result.getId());
        verify(ratingCategoryService).getRatingCategoryById(id);
    }

    @Test
    void createRatingCategory_ReturnsMappedDto() {
        RatingCategoryCreateDto createDto = RatingCategoryCreateDto.builder()
            .name("Visuals")
            .build();

        RatingCategory entity = RatingCategory.builder()
            .id(UUID.randomUUID())
            .name("Visuals")
            .build();

        when(ratingCategoryService.createRatingCategory(
                argThat(e -> e.getName().equals("Visuals"))
        )).thenReturn(entity);

        RatingCategoryDto result = ratingCategoryFacade.createRatingCategory(createDto);

        assertNotNull(result);
        assertEquals("Visuals", result.getName());
        verify(ratingCategoryService).createRatingCategory(
            argThat(e -> e.getName().equals("Visuals"))
        );
        verify(ratingCategoryEventProducer).sendRatingCategoryCreatedEvent(
            argThat(event -> event.getName().equals("Visuals"))
        );
    }

    @Test
    void createRatingCategory_MapsDtoToEntityCorrectly() {
        RatingCategoryCreateDto createDto = RatingCategoryCreateDto.builder()
            .name("Direction")
            .build();

        RatingCategory entity = ratingCategoryCreateDtoMapper.ratingCategoryCreateDtoToRatingCategory(createDto);

        assertNotNull(entity);
        assertEquals("Direction", entity.getName());
    }

    @Test
    void createRatingCategory_MapsEntityToDtoCorrectly() {
        RatingCategory entity = RatingCategory.builder()
            .id(UUID.randomUUID())
            .name("Cinematography")
            .build();

        RatingCategoryDto dto = ratingCategoryMapper.ratingCategoryToRatingCategoryDto(entity);

        assertNotNull(dto);
        assertEquals("Cinematography", dto.getName());
        assertEquals(entity.getId(), dto.getId());
    }
}
