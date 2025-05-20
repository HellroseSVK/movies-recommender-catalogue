package org.movierc.recommendationservice.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(
        callSuper = false,
        doNotUseGetters = true
)
public class RatingCategory {
    @Id
    private UUID id;
    private String name;

    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "ratingCategory", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<RatingDetail> ratingDetails;
}
