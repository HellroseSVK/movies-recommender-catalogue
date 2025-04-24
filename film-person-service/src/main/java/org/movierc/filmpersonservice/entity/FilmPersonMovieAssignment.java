package org.movierc.filmpersonservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.movierc.shared.enm.FilmPersonRole;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmPersonMovieAssignment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID movieId;

    private FilmPersonRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    private FilmPerson filmPerson;
}
