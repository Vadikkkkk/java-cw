package com.example.task_java.repository;

import com.example.task_java.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRep {
    public List<User> findAll();
    public User save(User user);
    public Optional<User> findById(Long userId);
    public void deleteById(long userId);
    public boolean existsById(long userId);
    public boolean existsByEmailIgnoreCase(String email);
    public Optional<User> findByEmailIgnoreCase(String email);
}
