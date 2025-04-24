package org.movierc.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.userservice.entity.User;
import org.movierc.userservice.exception.UserNotFoundException;
import org.movierc.userservice.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository);
    }

    @Test
    public void getUsers_WhenUsersPresent_ReturnsUserList() {
        // Setup
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setPassword("pass1");
        user1.setEmail("user1@example.com");
        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setPassword("pass2");
        user2.setEmail("user2@example.com");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        // Act
        List<User> result = userService.getUsers();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void createUser_WhenUserNotExists_ReturnsUser() {
        // Setup
        User userInput = new User();
        userInput.setUsername("newUser");
        userInput.setPassword("secret");
        userInput.setEmail("newUser@example.com");

        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername("newUser");
        savedUser.setPassword("secret");
        savedUser.setEmail("newUser@example.com");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        User result = userService.createUser(userInput);

        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).save(userInput);
    }

    @Test
    public void getUserById_WhenUserExists_ReturnsUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("user");
        user.setPassword("pass");
        user.setEmail("user@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.getUserById(userId);

        // Verify
        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void getUserById_WhenUserDoesNotExist_ThrowsUserNotFoundException() {
        // Setup
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Verify
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById(userId));
        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void updateUser_WhenUserExists_ReturnsUpdatedUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        User originalUser = new User();
        originalUser.setId(userId);
        originalUser.setUsername("oldUser");
        originalUser.setPassword("oldPass");
        originalUser.setEmail("old@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(originalUser));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("updatedUser");
        updatedUser.setPassword("newPass");
        updatedUser.setEmail("updated@example.com");
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userService.updateUser(userId, "updatedUser", "updated@example.com", "newPass");

        // Verify
        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(originalUser);
    }

    @Test
    public void updateUser_WhenUserDoesNotExist_ThrowsUserNotFoundException() {
        // Setup
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Verify
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.updateUser(userId, "updatedUser", "updated@example.com", "newPass"));
        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void deleteUser_WhenUserExists_DeletesUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("deleteMe");
        user.setEmail("delete@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        User result = userService.deleteUser(userId);

        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(userId);
        assertEquals(user.getId(), result.getId());
    }

    @Test
    public void deleteUser_WhenUserDoesNotExist_ThrowsUserNotFoundException() {
        // Setup
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Verify
        UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(userId));
        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(any(UUID.class));
    }
}