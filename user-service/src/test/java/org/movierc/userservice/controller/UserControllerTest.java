package org.movierc.userservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.facade.UserFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserFacade userFacade;

    @Test
    public void getAllUsers_HaveUsers_ReturnsOkWithList() {
        // Setup
        UserDto sampleUser = UserDto.builder()
                .id(UUID.randomUUID())
                .username("user1")
                .password("secret")
                .email("user1@example.com")
                .build();
        List<UserDto> userList = Collections.singletonList(sampleUser);
        when(userFacade.getAllUsers()).thenReturn(userList);

        // Act
        ResponseEntity<List<UserDto>> response = userController.getAllUsers();

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userList, response.getBody());
        verify(userFacade, times(1)).getAllUsers();
    }

    @Test
    public void getUserById_UserExists_ReturnsOkWithUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserDto sampleUser = UserDto.builder()
                .id(userId)
                .username("user1")
                .password("secret")
                .email("user1@example.com")
                .build();
        when(userFacade.getUserById(userId)).thenReturn(sampleUser);

        // Act
        ResponseEntity<UserDto> response = userController.getUserById(userId);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleUser, response.getBody());
        verify(userFacade, times(1)).getUserById(userId);
    }

    @Test
    public void getUserById_UserNotFound_ThrowsException() {
        // Setup
        UUID userId = UUID.randomUUID();
        RuntimeException exception = new RuntimeException("User not found");
        when(userFacade.getUserById(userId)).thenThrow(exception);

        // Act & Verify
        Exception thrown = assertThrows(RuntimeException.class, () -> userController.getUserById(userId));
        assertEquals("User not found", thrown.getMessage());
        verify(userFacade, times(1)).getUserById(userId);
    }

    @Test
    public void createUser_ValidInput_ReturnsOkWithUser() {
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
        when(userFacade.createUser(createDto)).thenReturn(expectedUser);

        // Act
        ResponseEntity<UserDto> response = userController.createUser(createDto);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
        verify(userFacade, times(1)).createUser(createDto);
    }

    @Test
    public void createUser_InvalidInput_ThrowsException() {
        // Setup
        UserCreateDto createDto = UserCreateDto.builder()
                .username("")
                .password("secret")
                .email("invalid@example.com")
                .build();
        RuntimeException exception = new RuntimeException("Invalid input");
        when(userFacade.createUser(createDto)).thenThrow(exception);

        // Act & Verify
        Exception thrown = assertThrows(RuntimeException.class, () -> userController.createUser(createDto));
        assertEquals("Invalid input", thrown.getMessage());
        verify(userFacade, times(1)).createUser(createDto);
    }

    @Test
    public void updateUser_ValidInput_ReturnsOkWithUser() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .username("updatedUser")
                .password("updatedSecret")
                .email("updated@example.com")
                .build();
        UserDto expectedUser = UserDto.builder()
                .id(userId)
                .username("updatedUser")
                .password("updatedSecret")
                .email("updated@example.com")
                .build();
        when(userFacade.updateUser(userId, updateDto)).thenReturn(expectedUser);

        // Act
        ResponseEntity<UserDto> response = userController.updateUser(userId, updateDto);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedUser, response.getBody());
        verify(userFacade, times(1)).updateUser(userId, updateDto);
    }

    @Test
    public void updateUser_UserNotFound_ThrowsException() {
        // Setup
        UUID userId = UUID.randomUUID();
        UserUpdateDto updateDto = UserUpdateDto.builder()
                .username("updatedUser")
                .password("updatedSecret")
                .email("updated@example.com")
                .build();
        RuntimeException exception = new RuntimeException("User not found for update");
        when(userFacade.updateUser(userId, updateDto)).thenThrow(exception);

        // Act & Verify
        Exception thrown = assertThrows(RuntimeException.class, () -> userController.updateUser(userId, updateDto));
        assertEquals("User not found for update", thrown.getMessage());
        verify(userFacade, times(1)).updateUser(userId, updateDto);
    }

    @Test
    public void deleteUser_ValidId_DeletesUserAndReturnsNoContent() {
        // Setup
        UUID userId = UUID.randomUUID();

        // Act
        ResponseEntity<Void> response = userController.deleteUser(userId);

        // Verify
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userFacade, times(1)).deleteUser(userId);
    }
}