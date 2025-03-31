package org.movierc.movieservice.dto;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Valid
public class MovieUpdateDto {
    private String title;
    private String description;
    @Valid
    private List<String> images;
}