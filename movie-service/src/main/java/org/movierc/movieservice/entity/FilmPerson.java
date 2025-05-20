package org.movierc.movieservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(
        callSuper = false,
        doNotUseGetters = true
)
public class FilmPerson {
    @Id
    private UUID id;
    private String name;
    private URL image;
    private LocalDate dateOfBirth;
    private String biography;
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "filmPerson")
    private List<FilmPersonInMovieRole> filmPersonRoles;
}
