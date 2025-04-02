package org.movierc.feedbackservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.movierc.feedbackservice.dto.RatingCategoryDto;
import org.movierc.feedbackservice.facade.RatingCategoryFacade;
import org.springframework.http.HttpStatus;
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
    @Operation(
            summary = "Get all rating categories",
            description = "Get a list of all rating categories. The categories are used to rate movies."
    )
    @ApiResponses(
            value = {
                     @ApiResponse(responseCode = "200", description = "Rating categories retrieved successfully"),
                     @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<List<RatingCategoryDto>> getAllRatingCategories() {
        return ResponseEntity.ok(ratingCategoryFacade.getAllRatingCategories());
    }

    @GetMapping("{id}")
    @Operation(
            summary = "Get rating category by id",
            description = "Get a rating category by id. The id is the unique identifier of the category."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Rating category retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Rating category not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<RatingCategoryDto> getRatingCategoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(ratingCategoryFacade.getRatingCategoryById(id));
    }

    @PostMapping
    @Operation(
            summary = "Create rating category",
            description = "Create a new rating category. The category is used to rate movies."
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Rating category created successfully"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    public ResponseEntity<RatingCategoryDto> createRatingCategory(@RequestBody RatingCategoryDto ratingCategoryDto) {
        RatingCategoryDto createdCategory = ratingCategoryFacade.createRatingCategory(ratingCategoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }
}
