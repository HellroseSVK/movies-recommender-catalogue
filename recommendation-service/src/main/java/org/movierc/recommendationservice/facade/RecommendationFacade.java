package org.movierc.recommendationservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.mapper.MovieMapper;
import org.movierc.recommendationservice.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class RecommendationFacade {
    private final RecommendationService recommendationService;
    private final MovieMapper movieMapper;

    public List<MovieDto> getRecommendationsByCategoriesAboveThreshold(
            Set<UUID> categoryIds,
            int threshold
    ) {
        return recommendationService.getRecommendationsByCategoriesAboveThreshold(categoryIds, threshold)
                .entrySet()
                .stream()
                .map(e -> movieMapper.movieToMovieDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }

    public List<MovieDto> getRecommendationsByMovieId(UUID movieId) {
        return recommendationService.getRecommendationsByMovieId(movieId)
                .entrySet()
                .stream()
                .map(e -> movieMapper.movieToMovieDto(e.getKey(), e.getValue()))
                .collect(Collectors.toList());
    }
}
