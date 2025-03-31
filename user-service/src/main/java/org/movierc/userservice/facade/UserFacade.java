package org.movierc.userservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserFacade {
    private final UserService userService;

    public List<UserDto> getAllUsers() {
        return userService.getUsers();
    }

    public UserDto getUserById(UUID id) {
        return userService.getUserById(id);
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    public UserDto updateUser(UUID id, UserUpdateDto userUpdateDto) {
        return userService.updateUser(id, userUpdateDto);
    }

    public void deleteUser(UUID id) {
        userService.deleteUser(id);
    }
}
