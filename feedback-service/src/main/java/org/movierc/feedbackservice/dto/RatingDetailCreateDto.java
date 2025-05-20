package org.movierc.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDetailCreateDto {
    @NotNull
    private UUID ratingCategoryId;
    @Range(min = 0, max = 100)
    @Schema(example = "100", description = "Rating value between 0 and 100")
    private int rating;
}
