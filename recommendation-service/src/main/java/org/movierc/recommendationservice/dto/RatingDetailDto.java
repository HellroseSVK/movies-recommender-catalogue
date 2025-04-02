package org.movierc.recommendationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDetailDto {
    private RatingCategoryDto category;
    @Min(0)
    @Max(100)
    @Schema(example = "100")
    private int rating;
}