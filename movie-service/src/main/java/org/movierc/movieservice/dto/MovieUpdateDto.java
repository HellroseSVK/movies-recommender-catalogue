package org.movierc.movieservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieUpdateDto {
    @NotBlank
    @Schema(example = "The Matrix")
    private String title;
    @Schema(example = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.")
    private String description;
    @Schema(example = "[\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]")
    @NotNull
    private List<URL> images;
}