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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "text/plain"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userFacade.getUserById(id));
    }

    @Operation(summary = "Create new user", description = "Create a new user with the provided information.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User created successfully", content = @Content(schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "text/plain"))
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return ResponseEntity.ok(userFacade.createUser(userCreateDto));
    }

    @Operation(summary = "Delete user", description = "Delete a user by their ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "text/plain"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userFacade.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update user", description = "Update an existing user with new information.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(mediaType = "text/plain")),
        @ApiResponse(responseCode = "404", description = "User not found", content = @Content(mediaType = "text/plain"))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userFacade.updateUser(id, userUpdateDto));
    }
}
