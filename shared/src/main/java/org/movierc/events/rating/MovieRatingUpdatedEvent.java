package org.movierc.events.rating;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
public class MovieRatingUpdatedEvent {
    private UUID movieId;
    private UUID userId;
    private List<RatingDetail> newRatingDetails;
    private List<RatingDetail> oldRatingDetails;
}
