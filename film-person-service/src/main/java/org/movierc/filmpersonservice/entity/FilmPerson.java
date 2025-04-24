package org.movierc.filmpersonservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
public class FilmPerson {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private URL image;

    private LocalDate dateOfBirth;

    private String biography;

    @OneToMany(mappedBy = "filmPerson", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<FilmPersonMovieAssignment> movies;
}
