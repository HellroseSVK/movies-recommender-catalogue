package org.movierc.recommendationservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.movierc.recommendationservice.entity.RatingCategory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class RatingCategoryRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RatingCategoryRepository ratingCategoryRepository;

    @MockitoBean
    private SecurityFilterChain securityFilterChain;

    private RatingCategory category1;
    private RatingCategory category2;

    @BeforeEach
    void setUp() {
        // Setup: persist two rating categories
        category1 = RatingCategory.builder()
                .id(UUID.randomUUID())
                .name("Plot")
                .build();
        entityManager.persist(category1);

        category2 = RatingCategory.builder()
                .id(UUID.randomUUID())
                .name("Cinematography")
                .build();
        entityManager.persist(category2);

        entityManager.flush();
    }

    @Test
    void findByIdIn_ExistingIds_ReturnsRatingCategories() {
        // Act
        Set<UUID> ids = Set.of(category1.getId(), category2.getId());
        List<RatingCategory> result = ratingCategoryRepository.findByIdIn(ids);

        // Verify
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(cat -> cat.getId().equals(category1.getId())));
        assertTrue(result.stream().anyMatch(cat -> cat.getId().equals(category2.getId())));
    }

    @Test
    void findByIdIn_NonExistingIds_ReturnsEmpty() {
        // Act
        Set<UUID> nonExistingIds = Set.of(UUID.randomUUID());
        List<RatingCategory> result = ratingCategoryRepository.findByIdIn(nonExistingIds);

        // Verify
        assertTrue(result.isEmpty());
    }

    @Test
    void findByIdIn_PartiallyExistingIds_ReturnsOnlyExisting() {
        // Act
        Set<UUID> ids = Set.of(category1.getId(), UUID.randomUUID());
        List<RatingCategory> result = ratingCategoryRepository.findByIdIn(ids);

        // Verify
        assertEquals(1, result.size());
        assertEquals(category1.getId(), result.get(0).getId());
    }
}
