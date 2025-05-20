package org.movierc.filmpersonservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.net.URL;
import java.time.LocalDate;
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
public class FilmPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Exclude
    private UUID id;

    private String name;

    private URL image;

    private LocalDate dateOfBirth;

    private String biography;

    @OneToMany(mappedBy = "filmPerson", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @EqualsAndHashCode.Exclude
    private List<FilmPersonMovieAssignment> movies;
}
