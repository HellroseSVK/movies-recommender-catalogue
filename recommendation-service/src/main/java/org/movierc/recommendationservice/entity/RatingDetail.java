package org.movierc.recommendationservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RatingDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int rating; // 0-100

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private RatingCategory ratingCategory;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private UserMovieRating userMovieRating;
}