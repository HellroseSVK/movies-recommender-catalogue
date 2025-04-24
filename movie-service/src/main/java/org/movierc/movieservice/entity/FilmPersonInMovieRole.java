package org.movierc.movieservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.movierc.shared.enm.FilmPersonRole;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FilmPersonInMovieRole {
    @Id
    private UUID id;
    @ManyToOne
    private Movie movie;
    @ManyToOne
    private FilmPerson filmPerson;
    private FilmPersonRole role;
}
