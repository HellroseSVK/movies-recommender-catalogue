package org.movierc.recommendationservice.rabbit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.events.rating.RatingCategoryCreatedEvent;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.movierc.recommendationservice.mapper.RatingCategoryMapper;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RatingCategoryEventConsumerTest {
    @Mock
    private RatingCategoryRepository ratingCategoryRepository;

    @Mock
    private RatingCategoryMapper ratingCategoryMapper;

    private RatingCategoryEventConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new RatingCategoryEventConsumer(ratingCategoryRepository, ratingCategoryMapper);
    }

    @Test
    void handleRatingCategoryCreatedEvent_CategoryNotExists_SavesCategory() {
        UUID categoryId = UUID.randomUUID();
        RatingCategoryCreatedEvent event = RatingCategoryCreatedEvent.builder()
            .id(categoryId)
            .name("Test Category")
            .build();
        RatingCategory entity = RatingCategory.builder()
            .id(categoryId)
            .name("Test Category")
            .build();

        when(ratingCategoryRepository.existsById(categoryId)).thenReturn(false);
        when(ratingCategoryMapper.ratingCategoryCreatedEventToRatingCategory(event)).thenReturn(entity);

        consumer.handleRatingCategoryCreatedEvent(event);

        verify(ratingCategoryRepository, times(1)).save(entity);
    }

    @Test
    void handleRatingCategoryCreatedEvent_CategoryExists_DoesNotSaveCategory() {
        UUID categoryId = UUID.randomUUID();
        RatingCategoryCreatedEvent event = RatingCategoryCreatedEvent.builder()
            .id(categoryId)
            .name("Test Category")
            .build();

        when(ratingCategoryRepository.existsById(categoryId)).thenReturn(true);

        consumer.handleRatingCategoryCreatedEvent(event);

        verify(ratingCategoryRepository, never()).save(any());
    }
}