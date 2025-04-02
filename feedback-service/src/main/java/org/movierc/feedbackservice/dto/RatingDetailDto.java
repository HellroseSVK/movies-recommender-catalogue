package org.movierc.feedbackservice.dto;

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
    @Schema(example = "100", description = "Rating value between 0 and 100")
    private int rating;
}
