package org.movierc.feedbackservice.repository;

import org.movierc.feedbackservice.entity.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepository {
    private final List<User> users = new ArrayList<>();

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public Optional<User> findById(UUID id) {
        return users.stream().filter(u -> u.getId().equals(id)).findFirst();
    }

    public boolean existsById(UUID id) {
        return users.stream().anyMatch(u -> u.getId().equals(id));
    }

    public void deleteById(UUID id) {
        users.removeIf(u -> u.getId().equals(id));
    }

    public User save(User user) {
        if (users.stream().anyMatch(u -> u.getId().equals(user.getId()))) {
            users.removeIf(u -> u.getId().equals(user.getId()));
        }

        users.add(user);
        return user;
    }
}
