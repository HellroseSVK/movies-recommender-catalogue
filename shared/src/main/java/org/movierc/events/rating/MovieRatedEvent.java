package org.movierc.events.rating;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@Setter
@ToString
public class MovieRatedEvent {
    private UUID movieId;
    private UUID userId;
    private float rating;
}
