package com.example.task_java.service;

import com.example.task_java.model.User;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public interface UserService {
    public List<User> findAllUsers();
    public User registerUser(User user);
    public User findUserById(long userId);
    public User loginUser(String email);
    public void deleteUser (long userId);
}
