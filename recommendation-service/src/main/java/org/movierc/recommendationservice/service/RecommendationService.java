package org.movierc.recommendationservice.service;

import lombok.AllArgsConstructor;
import org.javatuples.Triplet;
import org.movierc.recommendationservice.dto.GetRecommendationByCategoriesWeightedDto;
import org.movierc.recommendationservice.dto.MovieDto;
import org.movierc.recommendationservice.entity.Movie;
import org.movierc.recommendationservice.entity.RatingDetail;
import org.movierc.recommendationservice.entity.UserMovieRating;
import org.movierc.recommendationservice.exception.CategoryNotFoundException;
import org.movierc.recommendationservice.exception.MovieNotFoundException;
import org.movierc.recommendationservice.mapper.MovieMapper;
import org.movierc.recommendationservice.mapper.RatingCategoryMapper;
import org.movierc.recommendationservice.mapper.RatingDetailMapper;
import org.movierc.recommendationservice.repository.MovieRepository;
import org.movierc.recommendationservice.repository.RatingCategoryRepository;
import org.movierc.recommendationservice.repository.UserMovieRatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RecommendationService {
    private final MovieRepository movieRepository;
    private final RatingCategoryRepository ratingCategoryRepository;
    private final UserMovieRatingRepository userMovieRatingRepository;

    private final MovieMapper movieMapper;
    private final RatingDetailMapper ratingDetailMapper;
    private final RatingCategoryMapper ratingCategoryMapper;

    private MovieDto transformTripletToMovieDto(Triplet<Movie, List<UserMovieRating>, Double> triplet) {
        var movie = triplet.getValue0();
        var userRatings = getAverageRatingDetailForMovieFromUserRatings(triplet.getValue1()).stream()
                .map(ratingDetail -> {
                    var categoryDto = ratingCategoryMapper.ratingCategoryToRatingCategoryDto(
                            ratingCategoryRepository.findById(ratingDetail.getRatingCategoryId())
                                    .orElseThrow(() -> new CategoryNotFoundException(ratingDetail.getRatingCategoryId())));
                    return ratingDetailMapper.ratingDetailToRatingDetailDto(ratingDetail, categoryDto);
                })
                .toList();
        return movieMapper.movieToMovieDto(movie, userRatings);
    }

    private List<RatingDetail> getAverageRatingDetailForMovieFromUserRatings(List<UserMovieRating> userRatings) {
        return userRatings.stream()
                .flatMap(userRating -> userRating.getRatingDetails().stream())
                .collect(Collectors.groupingBy(RatingDetail::getRatingCategoryId, Collectors.averagingDouble(RatingDetail::getRating)))
                .entrySet().stream()
                .map(entry -> RatingDetail.builder()
                        .ratingCategoryId(entry.getKey())
                        .rating(entry.getValue().intValue())
                        .build()).toList();
    }


    public List<MovieDto> getRecommendations() {
        return movieRepository.findAll().stream()
                .map(movie -> {
                    var userRatings = userMovieRatingRepository.findByMovieId(movie.getId());
                    var averageRating = userRatings.stream()
                            .mapToDouble(rating -> rating.getRatingDetails().stream()
                                    .mapToDouble(RatingDetail::getRating)
                                    .average()
                                    .orElse(0))
                            .average()
                            .orElse(0);
                    return new Triplet<>(movie, userRatings, averageRating);
                })
                .sorted((t1, t2) -> Double.compare(t2.getValue2(), t1.getValue2()))
                .map(this::transformTripletToMovieDto)
                .toList();
    }

    public List<MovieDto> getRecommendationsByCategoriesAboveThreshold(List<UUID> categoryIds, int threshold) {
        categoryIds.forEach(categoryId -> {
            if (!ratingCategoryRepository.existsById(categoryId)) {
                throw new CategoryNotFoundException(categoryId);
            }
        });

        return movieRepository.findAll().stream()
                .map(movie -> {
                    var userRatings = userMovieRatingRepository.findByMovieId(movie.getId());
                    var averageRating = userRatings.stream()
                            .mapToDouble(rating -> rating.getRatingDetails().stream()
                                    .filter(ratingDetail -> categoryIds.contains(ratingDetail.getRatingCategoryId()))
                                    .mapToDouble(RatingDetail::getRating)
                                    .average()
                                    .orElse(0))
                            .average()
                            .orElse(0);
                    return new Triplet<>(movie, userRatings, averageRating);
                })
                .filter(triplet -> triplet.getValue2() >= threshold)
                .sorted((t1, t2) -> Double.compare(t2.getValue2(), t1.getValue2()))
                .map(this::transformTripletToMovieDto)
                .toList();
    }

    public List<MovieDto> getRecommendationsByCategoriesWeighted(List<GetRecommendationByCategoriesWeightedDto> categoryWeights) {
        var categoryIds = categoryWeights.stream().map(GetRecommendationByCategoriesWeightedDto::getCategoryId).toList();

        categoryIds.forEach(categoryId -> {
            if (!ratingCategoryRepository.existsById(categoryId)) {
                throw new CategoryNotFoundException(categoryId);
            }
        });

        return movieRepository.findAll().stream()
                .map(movie -> {
                    var userRatings = userMovieRatingRepository.findByMovieId(movie.getId()).stream()
                            .filter(userMovieRating -> userMovieRating.getRatingDetails().stream()
                                    .anyMatch(ratingDetail -> categoryIds.contains(ratingDetail.getRatingCategoryId())))
                            .toList();
                    var averageRating = userRatings.stream()
                            .mapToDouble(rating -> rating.getRatingDetails().stream()
                                    .mapToDouble(ratingDetail -> ratingDetail.getRating() * categoryWeights.stream()
                                            .filter(categoryWeight -> categoryWeight.getCategoryId().equals(ratingDetail.getRatingCategoryId()))
                                            .findFirst()
                                            // should not happen as we already filtered by categories inside categoryWeight
                                            .orElseThrow(() -> new CategoryNotFoundException(UUID.fromString("unknown")))
                                            .getWeight())
                                    .average()
                                    .orElse(0))
                            .average()
                            .orElse(0);
                    return new Triplet<>(movie, userRatings, averageRating);
                })
                .sorted((t1, t2) -> Double.compare(t2.getValue2(), t1.getValue2()))
                .map(this::transformTripletToMovieDto)
                .toList();
    }

    public List<MovieDto> getRecommendationsByRated(UUID userId) {
        return movieRepository.findAll().stream()
                .map(movie -> {
                    var userRatings = userMovieRatingRepository.findByMovieId(movie.getId()).stream()
                            .filter(userMovieRating -> userMovieRating.getUserId().equals(userId))
                            .toList();
                    var averageRating = userRatings.stream()

                            .mapToDouble(rating -> rating.getRatingDetails().stream()
                                    .mapToDouble(RatingDetail::getRating)
                                    .average()
                                    .orElse(0))
                            .average()
                            .orElse(0);
                    return new Triplet<>(movie, userRatings, averageRating);
                })
                .sorted((t1, t2) -> Double.compare(t2.getValue2(), t1.getValue2()))
                .map(this::transformTripletToMovieDto)
                .toList();
    }

    public List<MovieDto> getRecommendationsByRatedAllSimilarAboveThreshold(UUID userId, int threshold) {
        var interestedCategories = userMovieRatingRepository.findByUserId(userId).stream()
                .flatMap(userMovieRating -> userMovieRating.getRatingDetails().stream())
                .filter(ratingDetail -> ratingDetail.getRating() >= threshold)
                .map(RatingDetail::getRatingCategoryId)
                .distinct()
                .toList();
        return getRecommendationsByCategoriesAboveThreshold(interestedCategories, 0);
    }

    public List<MovieDto> getRecommendationsByMovieId(UUID movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new MovieNotFoundException(movieId);
        }

        var userIds = userMovieRatingRepository.findByMovieId(movieId).stream()
                .map(UserMovieRating::getUserId)
                .toList();

        return movieRepository.findAll().stream()
                .map(movie -> {
                    var userRatings = userMovieRatingRepository.findByMovieId(movie.getId()).stream()
                            .filter(userMovieRating -> userIds.contains(userMovieRating.getUserId()))
                            .toList();
                    var averageRating = userRatings.stream()
                            .mapToDouble(rating -> rating.getRatingDetails().stream()
                                    .mapToDouble(RatingDetail::getRating)
                                    .average()
                                    .orElse(0))
                            .average()
                            .orElse(0);
                    return new Triplet<>(movie, userRatings, averageRating);
                })
                .filter(triplet -> !triplet.getValue0().getId().equals(movieId))
                .sorted((t1, t2) -> Double.compare(t2.getValue2(), t1.getValue2()))
                .map(this::transformTripletToMovieDto)
                .toList();
    }
}