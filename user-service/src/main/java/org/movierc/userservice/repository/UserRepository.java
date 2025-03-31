package org.movierc.userservice.repository;

import org.movierc.userservice.entity.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();

    public Optional<User> findByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equals(username)).findFirst();
    }

    public Optional<User> findById(UUID id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public List<User> findAll() {
        return users;
    }

    public User save(User user) {
        if (users.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            users.removeIf(u -> u.getId().equals(user.getId()));
        }

        users.add(user);
        return user;
    }

    public void deleteById(UUID id) {
        users.removeIf(u -> u.getId().equals(id));
    }
}
