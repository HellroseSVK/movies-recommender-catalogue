package org.movierc.feedbackservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class UserMovieRatingCreateDto {
    @NotNull
    private UUID userId;
    @NotNull
    private UUID movieId;
    @NotNull
    private List<@Valid RatingDetailDto> ratingDetails;
    @Schema(example = "This movie was amazing!")
    private String comment;
}
