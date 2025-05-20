package org.movierc.feedbackservice.service;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.entity.RatingCategory;
import org.movierc.feedbackservice.exception.RatingCategoryNotFoundException;
import org.movierc.feedbackservice.repository.RatingCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
@AllArgsConstructor
public class RatingCategoryService {
    private final RatingCategoryRepository ratingCategoryRepository;

    @Transactional(readOnly = true)
    public List<RatingCategory> getAllRatingCategories() {
        return ratingCategoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public RatingCategory getRatingCategoryById(UUID id) {
        return ratingCategoryRepository.findById(id)
                .orElseThrow(() -> new RatingCategoryNotFoundException(id));
    }

    public RatingCategory createRatingCategory(RatingCategory ratingCategory) {
        return ratingCategoryRepository.save(ratingCategory);
    }
}
