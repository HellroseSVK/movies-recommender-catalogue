package org.movierc.feedbackservice.repository;

import org.movierc.feedbackservice.entity.RatingCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RatingCategoryRepository extends JpaRepository<RatingCategory, UUID> {

}
