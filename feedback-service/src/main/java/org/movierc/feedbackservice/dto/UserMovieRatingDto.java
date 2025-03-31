package org.movierc.feedbackservice.dto;

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
    private UUID id;
    private UserDto user;
    private MovieDto movie;
    private List<RatingDetailDto> ratingDetails;
    private String comment;
}
