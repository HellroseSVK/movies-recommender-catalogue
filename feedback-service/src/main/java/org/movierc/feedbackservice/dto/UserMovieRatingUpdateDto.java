package org.movierc.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class UserMovieRatingUpdateDto {
    @NotNull
    private List<@Valid RatingDetailDto> ratingDetails;
    @Schema(example = "This movie was amazing!")
    private String comment;
}
