package org.movierc.feedbackservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

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
public class Movie {
    @Id
    @EqualsAndHashCode.Exclude
    private UUID id;
    private String title;
}
