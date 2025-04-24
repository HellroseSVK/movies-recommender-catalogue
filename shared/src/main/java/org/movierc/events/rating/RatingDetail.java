package org.movierc.events.rating;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class RatingDetail implements Serializable {
    private UUID ratingCategoryId;
    private int rating; // 0-100
}
