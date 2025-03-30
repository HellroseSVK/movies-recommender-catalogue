package org.movierc.filmpersonservice.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class FilmPersonCreateDto {
    private String name;
    private String image;
    private LocalDate dateOfBirth;
    private String biography;
}
