package com.example.task_java.repository.implementations;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.model.User;
import com.example.task_java.repository.UserRep;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class UserRepImplementation implements UserRep {

    private final List<User> users = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public List<User> findAllUsers() {
        return List.copyOf(users);
    }

    @Override
    public User saveUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (existsByEmail(user.getEmail())) {
            throw new DoubleRecordException(
                    "User with this email already exists.");
        }
        user.setUserId(idCounter.incrementAndGet());
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> findUserById(Long userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public boolean deleteUser(long userId) {
        return users.removeIf(user -> user.getUserId() == userId);
    }

    @Override
    public boolean existsById(long userId) {
        return users.stream()
                .anyMatch(user -> user.getUserId().equals(userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}