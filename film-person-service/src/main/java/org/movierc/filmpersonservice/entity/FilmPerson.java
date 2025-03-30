package org.movierc.filmpersonservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilmPerson {
    private UUID id;
    private String name;
    private String image;
    private LocalDate dateOfBirth;
    private String biography;

    private List<FilmPersonMovieAssignment> movies;
}
