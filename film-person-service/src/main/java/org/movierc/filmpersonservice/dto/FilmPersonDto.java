package org.movierc.filmpersonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
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
    @Schema(example = "John Doe")
    @NotBlank
    private String name;
    @Schema(example = "http://example.com/image1.jpg")
    @NotNull
    private URL image;
    @Schema(example = "1980-01-01")
    @NotNull
    private LocalDate dateOfBirth;
    @Schema(example = "John Doe is a famous actor known for his roles in various movies.")
    @NotBlank
    private String biography;
    @NotNull
    private List<@Valid FilmPersonMovieAssignmentDto> movies;
}
