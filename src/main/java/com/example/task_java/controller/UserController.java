package com.example.task_java.controller;

import com.example.task_java.dto.UserRegistrationRequest;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.User;
import com.example.task_java.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody UserRegistrationRequest registrationRequest) {
        try {
            if (registrationRequest.getEmail() == null || registrationRequest.getEmail().isBlank()) {
                throw new IllegalArgumentException("Invalid email.");
            }
            User newUser = User.builder()
                    .userName(registrationRequest.getUserName())
                    .email(registrationRequest.getEmail())
                    .build();

            User createdUser = userService.registerUser(newUser);
            return ResponseEntity
                    .created(URI.create("/users/" + createdUser.getUserId()))
                    .body(createdUser);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }

    @GetMapping("/login")
    public ResponseEntity<User> loginUser(@RequestParam String email) {
        try {
            User user = userService.loginUser(email);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found", e);
        }
    }

    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") long userId)
        throws RecordNotFoundException {

        try {
            User user = userService.findUserById(userId);
            return ResponseEntity.ok(user);

        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") long userId)
        throws RecordNotFoundException {

        try {
            userService.deleteUser(userId);
            return ResponseEntity.noContent().build();
        } catch (RecordNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        }
    }

}