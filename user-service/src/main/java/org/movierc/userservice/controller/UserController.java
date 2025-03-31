package org.movierc.userservice.controller;

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

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userFacade.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userFacade.getUserById(id));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserCreateDto userCreateDto) {
        return ResponseEntity.ok(userFacade.createUser(userCreateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userFacade.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @RequestBody @Valid UserUpdateDto userUpdateDto) {
        return ResponseEntity.ok(userFacade.updateUser(id, userUpdateDto));
    }
}
