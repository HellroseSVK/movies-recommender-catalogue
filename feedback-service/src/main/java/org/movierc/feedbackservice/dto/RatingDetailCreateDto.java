package org.movierc.feedbackservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RatingDetailCreateDto {
    @NotNull
    @Valid
    private RatingCategoryCreateDto category;
    @Range(min = 0, max = 100)
    private int rating;
}
