package org.movierc.feedbackservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(
        callSuper = false,
        doNotUseGetters = true
)
public class UserMovieRating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Exclude
    private UUID id;
    private String userId;
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private Movie movie;
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "rating", cascade = CascadeType.ALL)
    private List<RatingDetail> ratingDetails;
    private String comment;
}
