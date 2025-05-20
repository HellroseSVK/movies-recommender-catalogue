package org.movierc.movieservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.*;
import org.movierc.shared.enm.FilmPersonRole;

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
public class FilmPersonInMovieRole {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Exclude
    private UUID id;
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private Movie movie;
    @EqualsAndHashCode.Exclude
    @ManyToOne
    private FilmPerson filmPerson;
    private FilmPersonRole role;
}
