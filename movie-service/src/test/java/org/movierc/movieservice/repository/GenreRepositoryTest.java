package org.movierc.movieservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.movierc.movieservice.entity.Genre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class GenreRepositoryTest {
    @Autowired
    private GenreRepository genreRepository;

    @MockitoBean
    public SecurityFilterChain securityFilterChain;

    @BeforeEach
    void setUp() {
        // Setup
        Genre genre = new Genre();
        genre.setName("Comedy");
        genreRepository.save(genre);
    }

    @Test
    void whenFindByName_thenReturnGenre() {
        // Act
        Optional<Genre> found = genreRepository.findByName("Comedy");

        // Verify
        assertTrue(found.isPresent());
        assertEquals("Comedy", found.get().getName());
    }

    @Test
    void whenFindByNameNotExists_thenReturnEmptyOptional() {
        // Act
        Optional<Genre> found = genreRepository.findByName("NonExistent");

        // Verify
        assertFalse(found.isPresent());
    }
}