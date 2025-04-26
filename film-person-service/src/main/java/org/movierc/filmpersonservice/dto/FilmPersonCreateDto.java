package org.movierc.filmpersonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmPersonCreateDto {
    @NotBlank
    @Schema(example = "John Doe")
    private String name;
    @Schema(example = "http://example.com/image1.jpg")
    private URL image;
    @Schema(example = "1980-01-01")
    @NotNull
    @Past
    private LocalDate dateOfBirth;
    @Schema(example = "John Doe is a famous actor known for his roles in various movies.")
    private String biography;
}
