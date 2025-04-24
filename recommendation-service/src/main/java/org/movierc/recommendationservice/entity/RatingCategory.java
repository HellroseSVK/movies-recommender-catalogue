package org.movierc.recommendationservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RatingCategory {
    @Id
    private UUID id;
    private String name;

    @OneToMany(mappedBy = "ratingCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RatingDetail> ratingDetails;
}
