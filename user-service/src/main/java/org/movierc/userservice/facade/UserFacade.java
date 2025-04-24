package org.movierc.userservice.facade;

import lombok.AllArgsConstructor;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.mapper.UserCreateDtoMapper;
import org.movierc.userservice.mapper.UserMapper;
import org.movierc.userservice.rabbit.UserEventProducer;
import org.movierc.userservice.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
@Service
@AllArgsConstructor
public class UserFacade {
    private final UserService userService;

    private final UserEventProducer userEventProducer;

    private final UserCreateDtoMapper userCreateDtoMapper;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userService.getUsers().stream().map(userMapper::userToUserDto).toList();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id) {
        return userMapper.userToUserDto(userService.getUserById(id));
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        var savedUser = userService.createUser(userCreateDtoMapper.userCreateDtoToUser(userCreateDto));

        userEventProducer.sendUserCreatedEvent(userMapper.userToUserCreatedEvent(savedUser));

        return userMapper.userToUserDto(savedUser);
    }

    public UserDto updateUser(UUID id, UserUpdateDto userUpdateDto) {
        var updatedUser = userService.updateUser(id,
                userUpdateDto.getUsername(),
                userUpdateDto.getEmail(),
                userUpdateDto.getPassword());

        userEventProducer.sendUserUpdatedEvent(userMapper.userToUserUpdatedEvent(updatedUser));

        return userMapper.userToUserDto(updatedUser);
    }

    public void deleteUser(UUID id) {
        var deletedUser = userService.deleteUser(id);
        userEventProducer.sendUserDeletedEvent(userMapper.userToUserDeletedEvent(deletedUser));
    }
}
