package com.example.task_java.repository;

import com.example.task_java.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRep {
    public List<User> findAllUsers();
    public User saveUser(User user);
    public Optional<User> findUserById(Long userId);
    public boolean deleteUser(long userId);
    public boolean existsById(long userId);
    public boolean existsByEmail(String email);
    public Optional<User> findByEmail(String email);
}
