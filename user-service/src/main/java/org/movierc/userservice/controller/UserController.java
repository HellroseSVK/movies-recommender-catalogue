package org.movierc.userservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.facade.UserFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserFacade userFacade;

    @Operation(summary = "Get all users", description = "Retrieve a list of all users.")
    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userFacade.getAllUsers());
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a specific user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/problem+json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userFacade.getUserById(id));
    }

    @Operation(summary = "Create new user", description = "Create a new user with the provided information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/problem+json"))
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userFacade.createUser(userCreateDto));
    }

    @Operation(summary = "Delete user", description = "Delete a user by their ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/problem+json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userFacade.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user", description = "Update an existing user with new information.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "application/problem+json"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userFacade.updateUser(id, userUpdateDto));
    }
}
