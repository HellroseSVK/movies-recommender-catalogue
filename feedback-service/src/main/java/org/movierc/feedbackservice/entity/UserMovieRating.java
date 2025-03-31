package org.movierc.feedbackservice.entity;

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
@NoArgsConstructor
@AllArgsConstructor
public class UserMovieRating {
    private UUID id;
    private User user;
    private Movie movie;
    private List<RatingDetail> ratingDetails;
    private String comment;
}
