package org.movierc.filmpersonservice.entity;

import jakarta.persistence.*;
import lombok.*;
import org.movierc.shared.enm.FilmPersonRole;

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
public class FilmPersonMovieAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Exclude
    private UUID id;

    private UUID movieId;

    private FilmPersonRole role;

    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    private FilmPerson filmPerson;
}
