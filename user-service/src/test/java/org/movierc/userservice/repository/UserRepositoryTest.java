package org.movierc.userservice.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.movierc.userservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setEmail("testuser@example.com");
        userRepository.save(user);
    }

    @Test
    void whenFindByUsername_thenReturnUser() {
        // Act
        Optional<User> found = userRepository.findByUsername("testuser");

        // Verify
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
    }

    @Test
    void whenFindByUsername_thenReturnEmptyOptional() {
        // Act
        Optional<User> found = userRepository.findByUsername("NonExistent");

        // Verify
        assertFalse(found.isPresent());
    }
}