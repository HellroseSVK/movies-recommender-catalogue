package org.movierc.feedbackservice.controller;

import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.facade.RatingCategoryFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/v1/rating/category")
@RestController
@AllArgsConstructor
public class RatingCategoryController {
    private final RatingCategoryFacade ratingCategoryFacade;

    @GetMapping
    public ResponseEntity<List<RatingCategoryDto>> getAllRatingCategories() {
        return ResponseEntity.ok(ratingCategoryFacade.getAllRatingCategories());
    }

    @GetMapping("{id}")
    public ResponseEntity<RatingCategoryDto> getRatingCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(ratingCategoryFacade.getRatingCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<RatingCategoryDto> createRatingCategory(@RequestBody RatingCategoryDto ratingCategoryDto) {
        return ResponseEntity.ok(ratingCategoryFacade.createRatingCategory(ratingCategoryDto));
    }
}
