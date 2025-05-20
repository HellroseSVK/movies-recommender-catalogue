package org.movierc.movieservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
