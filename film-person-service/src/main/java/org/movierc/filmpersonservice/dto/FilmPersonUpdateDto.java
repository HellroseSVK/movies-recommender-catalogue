package org.movierc.filmpersonservice.dto;

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
public class FilmPersonUpdateDto {
    private String name;
    private String image;
    private LocalDate dateOfBirth;
    private String biography;
}
