package org.movierc.feedbackservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.RatingCategoryCreateDto;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.service.RatingCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RatingCategoryFacade {
    private final RatingCategoryService ratingCategoryService;

    public List<RatingCategoryDto> getAllRatingCategories() {
        return ratingCategoryService.getAllRatingCategories();
    }

    public RatingCategoryDto getRatingCategoryById(UUID id) {
        return ratingCategoryService.getRatingCategoryById(id);
    }

    public RatingCategoryDto createRatingCategory(RatingCategoryCreateDto ratingCategoryCreateDto) {
        return ratingCategoryService.createRatingCategory(ratingCategoryCreateDto);
    }
}
