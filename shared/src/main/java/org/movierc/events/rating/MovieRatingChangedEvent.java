package org.movierc.events.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MovieRatingChangedEvent implements Serializable {
    private UUID movieId;
    private String userId;
    private List<RatingDetail> newRatingDetails;
}
