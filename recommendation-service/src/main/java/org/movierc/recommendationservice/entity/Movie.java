package org.movierc.recommendationservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Movie {
    private UUID id;
    private String title;

//    private Map<UUID, List<RatingDetail>> userRatings;
//
//    public List<RatingDetail> getUserRatings() {
//        return userRatings.values().stream()
//                .flatMap(List::stream)
//                .collect(Collectors.groupingBy(
//                        RatingDetail::getRatingCategoryId,
//                        Collectors.averagingInt(RatingDetail::getRating)
//                ))
//                .entrySet().stream()
//                .map(entry -> new RatingDetail(entry.getKey(), entry.getValue().intValue()))
//                .collect(Collectors.toList());
//    }
}