package org.movierc.feedbackservice.dto;

import jakarta.validation.Valid;
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
    private UUID userId;
    private UUID movieId;
    private List<RatingDetailDto> ratingDetails;
    private String comment;
}
