package org.movierc.filmpersonservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
    private String name;
    @Schema(example = "http://example.com/image1.jpg")
    private URL image;
    private LocalDate dateOfBirth;
    private String biography;
}
