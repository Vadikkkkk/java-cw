package com.example.task_java.repository.implementations;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.User;
import com.example.task_java.repository.UserRep;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
@Profile("inmemory")
public class UserRepImplementation implements UserRep {

    private final List<User> users = new ArrayList<>();
    private static final AtomicLong idCounter = new AtomicLong();

    @Override
    public List<User> findAll() {
        return List.copyOf(users);
    }

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User can't be null");
        }

        if (existsByEmailIgnoreCase(user.getEmail())) {
            throw new DoubleRecordException(
                    "User with this email already exists.");
        }
        user.setUserId(idCounter.incrementAndGet());
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> findById(Long userId) {
        return users.stream()
                .filter(user -> user.getUserId().equals(userId))
                .findFirst();
    }

    @Override
    public void deleteById(long userId) {
        boolean removed = users.removeIf(user -> user.getUserId().equals(userId));
        if (!removed) {
            throw new RecordNotFoundException("User with id " + userId + " not found");
        }
    }

    @Override
    public boolean existsById(long userId) {
        return users.stream()
                .anyMatch(user -> user.getUserId().equals(userId));
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return users.stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public Optional<User> findByEmailIgnoreCase(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}