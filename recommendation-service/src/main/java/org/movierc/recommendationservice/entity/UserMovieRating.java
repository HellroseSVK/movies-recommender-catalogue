package org.movierc.recommendationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserMovieRating {
    private UUID userId;
    private UUID movieId;
    private List<RatingDetail> ratingDetails;
}
