package org.movierc.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.exception.UserAlreadyExistsException;
import org.movierc.userservice.exception.UserNotFoundException;
import org.movierc.userservice.mapper.UserCreateDtoMapper;
import org.movierc.userservice.mapper.UserMapper;
import org.movierc.userservice.entity.User;
import org.movierc.userservice.rabbit.UserEventProducer;
import org.movierc.userservice.repository.UserRepository;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @Mock
    private UserEventProducer userEventProducer;

    private final UserCreateDtoMapper userCreateDtoMapper = Mappers.getMapper(UserCreateDtoMapper.class);
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    private UserService userService;

    @BeforeEach
    public void setUp() {
        userService = new UserService(userRepository, userEventProducer, userCreateDtoMapper, userMapper);
    }

    @Test
    public void getUsers_WhenUsersPresent_ReturnsUserDtoList() {
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
        List<UserDto> result = userService.getUsers();

        // Verify
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void createUser_WhenUserNotExists_ReturnsUserDto() {
        // Setup
        UserCreateDto createDto = UserCreateDto.builder()
                .username("newUser")
                .password("secret")
                .email("newUser@example.com")
                .build();
        when(userRepository.findByUsername(createDto.getUsername())).thenReturn(Optional.empty());

        // Capture saved user to simulate repository save behavior
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername(createDto.getUsername());
        savedUser.setPassword(createDto.getPassword());
        savedUser.setEmail(createDto.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserDto result = userService.createUser(createDto);

        // Verify
        assertNotNull(result);
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUsername(), result.getUsername());
        verify(userRepository, times(1)).findByUsername(createDto.getUsername());
        verify(userRepository, times(1)).save(captor.capture());
        verify(userEventProducer, times(1)).sendUserCreatedEvent(any());
    }

    @Test
    public void createUser_WhenUserExists_ThrowsUserAlreadyExistsException() {
        // Setup
        UserCreateDto createDto = UserCreateDto.builder()
                .username("existingUser")
                .password("secret")
                .email("existing@example.com")
                .build();
        User existingUser = new User();
        existingUser.setId(UUID.randomUUID());
        existingUser.setUsername("existingUser");
        when(userRepository.findByUsername(createDto.getUsername())).thenReturn(Optional.of(existingUser));

        // Act & Verify
        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.createUser(createDto));
        assertEquals("User with username " + existingUser.getUsername() + " already exists", exception.getMessage());
        verify(userRepository, times(1)).findByUsername(createDto.getUsername());
        verify(userRepository, never()).save(any(User.class));
        verify(userEventProducer, never()).sendUserCreatedEvent(any());
    }

    @Test
    public void getUserById_WhenUserExists_ReturnsUserDto() {
        // Setup
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("user");
        user.setPassword("pass");
        user.setEmail("user@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDto result = userService.getUserById(userId);

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
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    public void updateUser_WhenUserExists_ReturnsUpdatedUserDto() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .username("updatedUser")
                .password("newSecret")
                .email("updated@example.com")
                .build();
        User user = new User();
        user.setId(userId);
        user.setUsername("oldUser");
        user.setPassword("oldSecret");
        user.setEmail("old@example.com");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername(updateDto.getUsername());
        updatedUser.setPassword(updateDto.getPassword());
        updatedUser.setEmail(updateDto.getEmail());
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        // Act
        UserDto result = userService.updateUser(userId, updateDto);

        // Verify
        assertNotNull(result);
        assertEquals(updateDto.getUsername(), result.getUsername());
        assertEquals(updateDto.getEmail(), result.getEmail());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).save(user);
        verify(userEventProducer, times(1)).sendUserUpdatedEvent(any());
    }

    @Test
    public void updateUser_WhenUserDoesNotExist_ThrowsUserNotFoundException() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .username("updatedUser")
                .password("newSecret")
                .email("updated@example.com")
                .build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Verify
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.updateUser(userId, updateDto));
        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any(User.class));
        verify(userEventProducer, never()).sendUserUpdatedEvent(any());
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
        userService.deleteUser(userId);

        // Verify
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).deleteById(user.getId());
        verify(userEventProducer, times(1)).sendUserDeletedEvent(any());
    }

    @Test
    public void deleteUser_WhenUserDoesNotExist_ThrowsUserNotFoundException() {
        // Setup
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Verify
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUser(userId));
        assertTrue(exception.getMessage().contains(userId.toString()));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).deleteById(any(UUID.class));
        verify(userEventProducer, never()).sendUserDeletedEvent(any());
    }
}