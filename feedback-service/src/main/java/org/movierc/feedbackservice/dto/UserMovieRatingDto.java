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
public class UserMovieRatingDto {
    @Valid
    private UUID id;
    private UserDto user;
    private MovieDto movie;
    private List< @Valid RatingDetailDto> ratingDetails;
    @NotNull
    @Schema(example = "This movie was amazing!")
    private String comment;
}
