package com.example.task_java.service.implementations;

import com.example.task_java.exception.DoubleRecordException;
import com.example.task_java.exception.RecordNotFoundException;
import com.example.task_java.model.User;
import com.example.task_java.repository.UserRep;
import com.example.task_java.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@AllArgsConstructor
public class UserServiceImplementation  implements UserService{

    private final UserRep userRep;

    @Override
    public List<User> findAllUsers() {
        return userRep.findAllUsers();
    }

    @Override
    public User registerUser(User user) throws DoubleRecordException {
        if (user == null) {
            throw new IllegalArgumentException("Invalid user! Can't be null");
        }
        return userRep.saveUser(user);
    }

    @Override
    public User findUserById(long userId) throws RecordNotFoundException {
        return userRep.findUserById(userId)
                .orElseThrow(() -> new RecordNotFoundException(
                        "User " + userId + " not found"));
    }

    @Override
    public User loginUser(String email) throws RecordNotFoundException {
        return userRep.findByEmail(email)
                .orElseThrow(() -> new RecordNotFoundException(
                        "User with email '" + email + "' not found."));
    }

    @Override
    public void deleteUser(long userId) throws RuntimeException{
        if (!userRep.existsById(userId)) {
            throw new RecordNotFoundException(
                    "User with id " + userId + " not found");
        }
        boolean isDeleted = userRep.deleteUser(userId);
    }

}
