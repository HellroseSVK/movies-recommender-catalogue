package org.movierc.recommendationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GetRecommendationByCategoriesWeightedDto {
    @NotNull
    private UUID categoryId;
    @Min(0)
    @Schema(example = "0.5")
    private Double weight;
}
