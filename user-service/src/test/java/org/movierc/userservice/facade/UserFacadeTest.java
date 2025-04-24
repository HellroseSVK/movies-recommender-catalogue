package org.movierc.userservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.entity.User;
import org.movierc.userservice.mapper.UserCreateDtoMapper;
import org.movierc.userservice.mapper.UserMapper;
import org.movierc.userservice.rabbit.UserEventProducer;
import org.movierc.userservice.service.UserService;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserFacadeTest {

    @InjectMocks
    private UserFacade userFacade;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserCreateDtoMapper userCreateDtoMapper;

    @Mock
    private UserEventProducer userEventProducer;

    @Test
    public void getAllUsers_HaveUsers_ReturnsUserList() {
        // Setup: prepare user entities and corresponding DTOs
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");
        user1.setPassword("secret1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");
        user2.setPassword("secret2");
        user2.setEmail("user2@example.com");

        UserDto user1Dto = UserDto.builder()
                .id(user1.getId())
                .username(user1.getUsername())
                .password(user1.getPassword())
                .email(user1.getEmail())
                .build();
        UserDto user2Dto = UserDto.builder()
                .id(user2.getId())
                .username(user2.getUsername())
                .password(user2.getPassword())
                .email(user2.getEmail())
                .build();

        when(userService.getUsers()).thenReturn(Arrays.asList(user1, user2));
        when(userMapper.userToUserDto(user1)).thenReturn(user1Dto);
        when(userMapper.userToUserDto(user2)).thenReturn(user2Dto);

        // Act
        List<UserDto> actualList = userFacade.getAllUsers();

        // Verify
        List<UserDto> expectedList = Arrays.asList(user1Dto, user2Dto);
        assertEquals(expectedList, actualList);
        verify(userService, times(1)).getUsers();
    }

    @Test
    public void getUserById_ExistingUser_ReturnsUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("user");
        user.setPassword("secret");
        user.setEmail("user@example.com");

        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .username("user")
                .password("secret")
                .email("user@example.com")
                .build();

        when(userService.getUserById(userId)).thenReturn(user);
        when(userMapper.userToUserDto(user)).thenReturn(expectedUser);

        // Act
        UserDto actualUser = userFacade.getUserById(userId);

        // Verify
        assertEquals(expectedUser, actualUser);
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    public void getUserById_NonExistingUser_ThrowsException() {
        // Setup
        UUID userId = UUID.randomUUID();
        RuntimeException exception = new RuntimeException("User not found");
        when(userService.getUserById(userId)).thenThrow(exception);

        // Act & Verify
        Exception thrown = assertThrows(RuntimeException.class, () -> userFacade.getUserById(userId));
        assertEquals("User not found", thrown.getMessage());
        verify(userService, times(1)).getUserById(userId);
    }

    @Test
    public void createUser_ValidInput_ReturnsCreatedUser() {
        // Setup
        UserCreateDto createDto = UserCreateDto.builder()
                .username("newUser")
                .password("secret")
                .email("newUser@example.com")
                .build();

        // Simulate mapping from DTO to entity.
        User mappedUser = new User();
        mappedUser.setUsername(createDto.getUsername());
        mappedUser.setPassword(createDto.getPassword());
        mappedUser.setEmail(createDto.getEmail());

        // Simulate service saving the user.
        User savedUser = new User();
        savedUser.setId(UUID.randomUUID());
        savedUser.setUsername(mappedUser.getUsername());
        savedUser.setPassword(mappedUser.getPassword());
        savedUser.setEmail(mappedUser.getEmail());

        UserDto expectedUser = UserDto.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .password(savedUser.getPassword())
                .email(savedUser.getEmail())
                .build();

        when(userCreateDtoMapper.userCreateDtoToUser(createDto)).thenReturn(mappedUser);
        when(userService.createUser(mappedUser)).thenReturn(savedUser);
        // Stub mapping to event and DTO (event object is not actually verified)
        when(userMapper.userToUserDto(savedUser)).thenReturn(expectedUser);
        doNothing().when(userEventProducer).sendUserCreatedEvent(any());

        // Act
        UserDto actualUser = userFacade.createUser(createDto);

        // Verify
        assertEquals(expectedUser, actualUser);
        verify(userCreateDtoMapper, times(1)).userCreateDtoToUser(createDto);
        verify(userService, times(1)).createUser(mappedUser);
        verify(userMapper, times(1)).userToUserCreatedEvent(savedUser);
        verify(userMapper, times(1)).userToUserDto(savedUser);
        verify(userEventProducer, times(1)).sendUserCreatedEvent(any());
    }

    @Test
    public void createUser_InvalidInput_ThrowsException() {
        // Setup
        UserCreateDto createDto = UserCreateDto.builder()
                .username("") // Assuming blank is invalid
                .password("secret")
                .email("invalidEmail")
                .build();
        RuntimeException exception = new RuntimeException("Invalid user input");
        when(userCreateDtoMapper.userCreateDtoToUser(createDto)).thenReturn(new User());
        when(userService.createUser(any())).thenThrow(exception);

        // Act & Verify
        Exception thrown = assertThrows(RuntimeException.class, () -> userFacade.createUser(createDto));
        assertEquals("Invalid user input", thrown.getMessage());
        verify(userService, times(1)).createUser(any());
    }

    @Test
    public void updateUser_ValidInput_ReturnsUpdatedUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .username("updatedUser")
                .password("newSecret")
                .email("updated@example.com")
                .build();

        // Simulate service update (facade maps the DTO into separate parameters)
        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername(updateDto.getUsername());
        updatedUser.setPassword(updateDto.getPassword());
        updatedUser.setEmail(updateDto.getEmail());

        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .username(updateDto.getUsername())
                .password(updateDto.getPassword())
                .email(updateDto.getEmail())
                .build();

        when(userService.updateUser(userId, updateDto.getUsername(), updateDto.getEmail(), updateDto.getPassword()))
                .thenReturn(updatedUser);
        when(userMapper.userToUserDto(updatedUser)).thenReturn(expectedUser);
        doNothing().when(userEventProducer).sendUserUpdatedEvent(any());

        // Act
        UserDto actualUser = userFacade.updateUser(userId, updateDto);

        // Verify
        assertEquals(expectedUser, actualUser);
        verify(userService, times(1))
                .updateUser(userId, updateDto.getUsername(), updateDto.getEmail(), updateDto.getPassword());
        verify(userMapper, times(1)).userToUserUpdatedEvent(updatedUser);
        verify(userMapper, times(1)).userToUserDto(updatedUser);
        verify(userEventProducer, times(1)).sendUserUpdatedEvent(any());
    }

    @Test
    public void updateUser_InvalidInput_ThrowsException() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .username("")
                .password("newSecret")
                .email("invalidEmail")
                .build();
        RuntimeException exception = new RuntimeException("Invalid update input");
        when(userService.updateUser(userId, updateDto.getUsername(), updateDto.getEmail(), updateDto.getPassword()))
                .thenThrow(exception);

        // Act & Verify
        Exception thrown = assertThrows(RuntimeException.class, () -> userFacade.updateUser(userId, updateDto));
        assertEquals("Invalid update input", thrown.getMessage());
        verify(userService, times(1))
                .updateUser(userId, updateDto.getUsername(), updateDto.getEmail(), updateDto.getPassword());
    }

    @Test
    public void deleteUser_ValidId_DeletesUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        // For delete, assume service returns a user to be mapped into a deleted event.
        User deletedUser = new User();
        deletedUser.setId(userId);
        deletedUser.setUsername("deleteMe");
        deletedUser.setEmail("delete@example.com");

        when(userService.deleteUser(userId)).thenReturn(deletedUser);
        doNothing().when(userEventProducer).sendUserDeletedEvent(any());

        // Act
        userFacade.deleteUser(userId);

        // Verify
        verify(userService, times(1)).deleteUser(userId);
        verify(userMapper, times(1)).userToUserDeletedEvent(deletedUser);
        verify(userEventProducer, times(1)).sendUserDeletedEvent(any());
    }
}