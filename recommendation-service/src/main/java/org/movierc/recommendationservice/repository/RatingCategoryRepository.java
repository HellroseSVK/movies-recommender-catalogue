package org.movierc.recommendationservice.repository;

import org.movierc.recommendationservice.entity.RatingCategory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RatingCategoryRepository {
    private final List<RatingCategory> ratingCategories = new ArrayList<>();

    public List<RatingCategory> findAll() {
        return new ArrayList<>(ratingCategories);
    }

    public Optional<RatingCategory> findById(UUID id) {
        return ratingCategories.stream().filter(rc -> rc.getId().equals(id)).findFirst();
    }

    public boolean existsById(UUID id) {
        return ratingCategories.stream().anyMatch(rc -> rc.getId().equals(id));
    }

    public void deleteById(UUID id) {
        ratingCategories.removeIf(rc -> rc.getId().equals(id));
    }

    public RatingCategory save(RatingCategory ratingCategory) {
        if (ratingCategories.stream().anyMatch(rc -> rc.getId().equals(ratingCategory.getId()))) {
            ratingCategories.removeIf(rc -> rc.getId().equals(ratingCategory.getId()));
        }

        ratingCategories.add(ratingCategory);
        return ratingCategory;
    }
}
