package org.movierc.filmpersonservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmPersonDto {
    @NotNull
    private UUID id;
    @NotBlank
    private String name;
    private String image;
    private LocalDate dateOfBirth;
    private String biography;

    private List<@Valid FilmPersonMovieAssignmentDto> movies;
}
