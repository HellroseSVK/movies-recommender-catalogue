package org.movierc.feedbackservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.RatingCategoryCreateDto;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.mapper.RatingCategoryCreateDtoMapper;
import org.movierc.feedbackservice.mapper.RatingCategoryMapper;
import org.movierc.feedbackservice.rabbit.RatingCategoryEventProducer;
import org.movierc.feedbackservice.service.RatingCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class RatingCategoryFacade {
    private final RatingCategoryService ratingCategoryService;

    private final RatingCategoryEventProducer ratingCategoryEventProducer;

    private final RatingCategoryMapper ratingCategoryMapper;
    private final RatingCategoryCreateDtoMapper ratingCategoryCreateDtoMapper;

    @Transactional(readOnly = true)
    public List<RatingCategoryDto> getAllRatingCategories() {
        return ratingCategoryService.getAllRatingCategories().stream()
            .map(ratingCategoryMapper::ratingCategoryToRatingCategoryDto)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RatingCategoryDto getRatingCategoryById(UUID id) {
        return ratingCategoryMapper.ratingCategoryToRatingCategoryDto(ratingCategoryService.getRatingCategoryById(id));
    }

    public RatingCategoryDto createRatingCategory(RatingCategoryCreateDto ratingCategoryCreateDto) {
        var ratingCategory = ratingCategoryService.createRatingCategory(ratingCategoryCreateDtoMapper.ratingCategoryCreateDtoToRatingCategory(ratingCategoryCreateDto));

        ratingCategoryEventProducer.sendRatingCategoryCreatedEvent(
          ratingCategoryMapper.ratingCategoryToRatingCategoryCreatedEvent(ratingCategory)
        );

        return ratingCategoryMapper.ratingCategoryToRatingCategoryDto(ratingCategory);
    }
}
