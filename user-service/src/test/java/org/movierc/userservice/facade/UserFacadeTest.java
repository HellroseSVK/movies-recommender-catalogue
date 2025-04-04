// File: `user-service/src/test/java/org/movierc/userservice/facade/UserFacadeTest.java`
package org.movierc.userservice.facade;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.service.UserService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserFacadeTest {

    @InjectMocks
    private UserFacade userFacade;

    @Mock
    private UserService userService;

    @Test
    public void getAllUsers_HaveUsers_ReturnsUserList() {
        // Setup
        UserDto user1 = UserDto.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .password("secret1")
                .email("user1@example.com")
                .build();
        UserDto user2 = UserDto.builder()
                .id(UUID.randomUUID())
                .username("user2")
                .password("secret2")
                .email("user2@example.com")
                .build();
        List<UserDto> expectedList = Arrays.asList(user1, user2);

        when(userService.getUsers()).thenReturn(expectedList);

        // Act
        List<UserDto> actualList = userFacade.getAllUsers();

        // Verify
        assertEquals(expectedList, actualList);
        verify(userService, times(1)).getUsers();
    }

    @Test
    public void getUserById_ExistingUser_ReturnsUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .username("user")
                .password("secret")
                .email("user@example.com")
                .build();
        when(userService.getUserById(userId)).thenReturn(expectedUser);

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
        UserDto expectedUser = UserDto.builder()
                .id(UUID.randomUUID())
                .username("newUser")
                .password("secret")
                .email("newUser@example.com")
                .build();
        when(userService.createUser(createDto)).thenReturn(expectedUser);

        // Act
        UserDto actualUser = userFacade.createUser(createDto);

        // Verify
        assertEquals(expectedUser, actualUser);
        verify(userService, times(1)).createUser(createDto);
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
        when(userService.createUser(createDto)).thenThrow(exception);

        // Act & Verify
        Exception thrown = assertThrows(RuntimeException.class, () -> userFacade.createUser(createDto));
        assertEquals("Invalid user input", thrown.getMessage());
        verify(userService, times(1)).createUser(createDto);
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
        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .username("updatedUser")
                .password("newSecret")
                .email("updated@example.com")
                .build();
        when(userService.updateUser(userId, updateDto)).thenReturn(expectedUser);

        // Act
        UserDto actualUser = userFacade.updateUser(userId, updateDto);

        // Verify
        assertEquals(expectedUser, actualUser);
        verify(userService, times(1)).updateUser(userId, updateDto);
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
        when(userService.updateUser(userId, updateDto)).thenThrow(exception);

        // Act & Verify
        Exception thrown = assertThrows(RuntimeException.class, () -> userFacade.updateUser(userId, updateDto));
        assertEquals("Invalid update input", thrown.getMessage());
        verify(userService, times(1)).updateUser(userId, updateDto);
    }

    @Test
    public void deleteUser_ValidId_DeletesUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        doNothing().when(userService).deleteUser(userId);

        // Act
        userFacade.deleteUser(userId);

        // Verify
        verify(userService, times(1)).deleteUser(userId);
    }
}