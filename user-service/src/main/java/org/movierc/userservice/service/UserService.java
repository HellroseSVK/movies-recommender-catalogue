package org.movierc.userservice.service;

import lombok.AllArgsConstructor;
import org.movierc.userservice.dto.UserCreateDto;
import org.movierc.userservice.dto.UserDto;
import org.movierc.userservice.dto.UserUpdateDto;
import org.movierc.userservice.exception.UserAlreadyExistsException;
import org.movierc.userservice.exception.UserNotFoundException;
import org.movierc.userservice.mapper.UserCreateDtoMapper;
import org.movierc.userservice.mapper.UserMapper;
import org.movierc.userservice.rabbit.UserEventProducer;
import org.movierc.userservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;
    private final UserCreateDtoMapper userCreateDtoMapper;
    private final UserMapper userMapper;

    public List<UserDto> getUsers() {
        return userRepository.findAll().stream().map(userMapper::userToUserDto).collect(Collectors.toList());
    }

    public UserDto createUser(UserCreateDto userCreateDto) {
        var existingUser = userRepository.findByUsername(userCreateDto.getUsername());
        existingUser.ifPresent(user -> {
            throw new UserAlreadyExistsException(user);
        });

        var user = userCreateDtoMapper.userCreateDtoToUser(userCreateDto);
        var savedUser = userRepository.save(user);
        userEventProducer.sendUserCreatedEvent(userMapper.userToUserCreatedEvent(savedUser));
        return userMapper.userToUserDto(savedUser);
    }

    public UserDto getUserById(UUID id) {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return userMapper.userToUserDto(user);
    }

    public UserDto updateUser(UUID id, UserUpdateDto userUpdateDto) {
        var user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setUsername(userUpdateDto.getUsername());
        user.setEmail(userUpdateDto.getEmail());
        user.setPassword(userUpdateDto.getPassword());
        var updatedUser = userRepository.save(user);
        userEventProducer.sendUserUpdatedEvent(userMapper.userToUserUpdatedEvent(updatedUser));
        return userMapper.userToUserDto(updatedUser);
    }

    public void deleteUser(UUID id) {
        var maybeUser = userRepository.findById(id);
        if (maybeUser.isEmpty()) {
            throw new UserNotFoundException(id);
        }
        var user = maybeUser.get();
        userRepository.deleteById(user.getId());
        userEventProducer.sendUserDeletedEvent(userMapper.userToUserDeletedEvent(user));
    }
}
