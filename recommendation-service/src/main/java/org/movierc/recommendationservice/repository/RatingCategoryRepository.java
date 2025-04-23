package org.movierc.recommendationservice.repository;

import org.movierc.recommendationservice.entity.RatingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface RatingCategoryRepository extends JpaRepository<RatingCategory, UUID> {
    List<RatingCategory> findByIdIn(Collection<UUID> ids);
}
